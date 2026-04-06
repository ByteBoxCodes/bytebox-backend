package com.byteboxcodes.byteboxbackend.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.config.DockerExecutionConfig;
import com.byteboxcodes.byteboxbackend.service.strategy.CodeExecutionStrategy;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DockerContainerPool {

    @Autowired
    private DockerExecutionConfig config;

    @Autowired
    private List<CodeExecutionStrategy> strategyList;

    private Map<String, CodeExecutionStrategy> strategies;
    
    // language -> queue of container IDs
    private final Map<String, BlockingQueue<String>> pool = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getLanguageName().toUpperCase(),
                        strategy -> strategy
                ));

        log.info("Initializing Docker Container Pool...");
        
        for (Map.Entry<String, CodeExecutionStrategy> entry : strategies.entrySet()) {
            String language = entry.getKey();
            CodeExecutionStrategy strategy = entry.getValue();
            String image = strategy.getDockerImage();
            
            BlockingQueue<String> containerQueue = new LinkedBlockingQueue<>(config.getPoolSizePerLanguage());
            
            for (int i = 0; i < config.getPoolSizePerLanguage(); i++) {
                // Docker container names cannot contain '+' signs
                String safeLangName = language.toLowerCase().replace("+", "p");
                String containerName = "bytebox-pool-" + safeLangName + "-" + i;
                try {
                    // pre-pull image
                    log.info("Pulling docker image {} ...", image);
                    Process pullProcess = new ProcessBuilder("docker", "pull", image).start();
                    pullProcess.waitFor();
                    
                    // remove if already exists
                    new ProcessBuilder("docker", "rm", "-f", containerName).start().waitFor();
                    
                    // start warm container
                    log.info("Starting warm container {} ...", containerName);
                    ProcessBuilder pb = new ProcessBuilder(
                            "docker", "run", "-d",
                            "--name", containerName,
                            "--log-driver", "none",
                            "--read-only",
                            "--tmpfs", "/tmp:rw,nosuid,exec,size=64m",
                            "--network", "none",
                            "--memory", "256m",
                            "--memory-swap", "256m",
                            "--cpus", "0.5",
                            "--pids-limit", "50",
                            "--ulimit", "nofile=64:64",
                            image,
                            "sleep", "infinity"
                    );
                    
                    Process process = pb.start();
                    int exitCode = process.waitFor();
                    
                    if (exitCode == 0) {
                        containerQueue.offer(containerName);
                        log.info("Successfully started container {}", containerName);
                    } else {
                        log.error("Failed to start container {}. Exit code: {}", containerName, exitCode);
                    }
                    
                } catch (Exception e) {
                    log.error("Error initializing container pool for {}: {}", language, e.getMessage());
                }
            }
            
            pool.put(language, containerQueue);
        }
    }

    @PreDestroy
    public void cleanup() {
        log.info("Shutting down Docker Container Pool...");
        for (BlockingQueue<String> queue : pool.values()) {
            while (!queue.isEmpty()) {
                String containerName = queue.poll();
                try {
                    log.info("Removing container {}", containerName);
                    new ProcessBuilder("docker", "rm", "-f", containerName).start().waitFor();
                } catch (Exception e) {
                    log.error("Failed to remove container {}: {}", containerName, e.getMessage());
                }
            }
        }
    }

    public String borrowContainer(String language) throws InterruptedException {
        BlockingQueue<String> queue = pool.get(language.toUpperCase());
        if (queue == null) {
            throw new IllegalArgumentException("No container pool for language: " + language);
        }
        return queue.take(); // blocks until a container is available
    }

    public void returnContainer(String language, String containerId) {
        BlockingQueue<String> queue = pool.get(language.toUpperCase());
        if (queue != null) {
            // Clean up the /tmp directory before returning
            try {
                new ProcessBuilder("docker", "exec", containerId, "sh", "-c", "rm -rf /tmp/* /tmp/.* 2>/dev/null").start().waitFor();
            } catch (Exception e) {
                log.warn("Failed to clean container {} before returning to pool: {}", containerId, e.getMessage());
            }
            queue.offer(containerId);
        }
    }
}
