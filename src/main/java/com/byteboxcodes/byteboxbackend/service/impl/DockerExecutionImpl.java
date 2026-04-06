package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;
import com.byteboxcodes.byteboxbackend.service.DockerExecutionService;
import com.byteboxcodes.byteboxbackend.service.ExecutionQueueService;
import com.byteboxcodes.byteboxbackend.service.strategy.CodeExecutionStrategy;

@Service
public class DockerExecutionImpl implements DockerExecutionService {

    private final Map<String, CodeExecutionStrategy> strategies;

    private final ExecutionQueueService executionQueueService;

    public DockerExecutionImpl(List<CodeExecutionStrategy> strategyList, ExecutionQueueService executionQueueService) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getLanguageName().toUpperCase(),
                        strategy -> strategy
                ));
        this.executionQueueService = executionQueueService;
    }

    @Override
    public DockerExecutionResult runCode(String language, String code, String input) {
        if (language == null) {
            return DockerExecutionResult.builder()
                    .stderr("Language not provided for execution")
                    .exitCode(-1)
                    .build();
        }

        CodeExecutionStrategy strategy = strategies.get(language.toUpperCase());

        if (strategy == null) {
            return DockerExecutionResult.builder()
                    .stderr("Unsupported language for local Docker execution: " + language)
                    .exitCode(-1)
                    .build();
        }

        try {
            // Wait for up to 15 seconds for queue + execution to complete
            return executionQueueService.submitExecution(strategy, code, input)
                    .get(15, java.util.concurrent.TimeUnit.SECONDS);
        } catch (java.util.concurrent.RejectedExecutionException e) {
            return DockerExecutionResult.builder()
                    .stderr("Server is busy. Execution queue is full. Please try again later.")
                    .exitCode(-1)
                    .build();
        } catch (java.util.concurrent.TimeoutException e) {
            return DockerExecutionResult.builder()
                    .stderr("Execution timed out.")
                    .exitCode(-1)
                    .isTimeout(true)
                    .build();
        } catch (Exception e) {
            return DockerExecutionResult.builder()
                    .stderr("System Execution Error: " + e.getMessage())
                    .exitCode(-1)
                    .build();
        }
    }
}
