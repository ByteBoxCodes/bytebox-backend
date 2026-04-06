package com.byteboxcodes.byteboxbackend.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.config.DockerExecutionConfig;
import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;
import com.byteboxcodes.byteboxbackend.service.strategy.CodeExecutionStrategy;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExecutionQueueService {

    private final DockerExecutionConfig config;
    private final DockerContainerPool containerPool;
    private final LinkedBlockingQueue<ExecutionTask> queue;
    private final Semaphore semaphore;
    private ExecutorService workerPool;

    public ExecutionQueueService(DockerExecutionConfig config, DockerContainerPool containerPool) {
        this.config = config;
        this.containerPool = containerPool;
        this.queue = new LinkedBlockingQueue<>(config.getQueueCapacity());
        this.semaphore = new Semaphore(config.getMaxConcurrentExecutions(), true); // fair semaphore
    }

    @PostConstruct
    public void init() {
        // Start worker threads equal to max concurrent executions + 1 (for queue management)
        workerPool = Executors.newFixedThreadPool(config.getMaxConcurrentExecutions() + 1);
        
        // Main queue consumer
        workerPool.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ExecutionTask task = queue.take(); // block until task available
                    
                    // submit task to worker pool, waiting for semaphore
                    workerPool.submit(() -> processTask(task));
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        if (workerPool != null) {
            workerPool.shutdownNow();
        }
    }

    public CompletableFuture<DockerExecutionResult> submitExecution(CodeExecutionStrategy strategy, String code, String input) {
        CompletableFuture<DockerExecutionResult> future = new CompletableFuture<>();
        
        ExecutionTask task = new ExecutionTask(strategy, code, input, future);
        
        boolean accepted = queue.offer(task);
        if (!accepted) {
            future.complete(DockerExecutionResult.builder()
                    .stderr("Server is busy. Execution queue is full. Please try again later.")
                    .exitCode(-1)
                    .build());
        }
        
        return future;
    }

    private void processTask(ExecutionTask task) {
        try {
            // Block until a concurrent execution slot opens up
            boolean acquired = semaphore.tryAcquire(config.getTimeoutSeconds() + 5, TimeUnit.SECONDS);
            
            if (!acquired) {
                task.future.complete(DockerExecutionResult.builder()
                        .stderr("Execution timed out while waiting in queue.")
                        .exitCode(-1)
                        .isTimeout(true)
                        .build());
                return;
            }
            
            String containerId = null;
            try {
                // Borrow container from pool
                containerId = containerPool.borrowContainer(task.strategy.getLanguageName());
                
                // We have a slot and a container! Execute actual code.
                DockerExecutionResult result = task.strategy.execute(task.code, task.input, containerId);
                task.future.complete(result);
            } catch (Exception e) {
                log.error("Execution error", e);
                task.future.completeExceptionally(e);
            } finally {
                // Always return container to pool and release semaphore
                if (containerId != null) {
                    containerPool.returnContainer(task.strategy.getLanguageName(), containerId);
                }
                semaphore.release();
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            task.future.completeExceptionally(e);
        }
    }

    private static class ExecutionTask {
        final CodeExecutionStrategy strategy;
        final String code;
        final String input;
        final CompletableFuture<DockerExecutionResult> future;

        ExecutionTask(CodeExecutionStrategy strategy, String code, String input, CompletableFuture<DockerExecutionResult> future) {
            this.strategy = strategy;
            this.code = code;
            this.input = input;
            this.future = future;
        }
    }
}
