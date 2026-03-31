package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;
import com.byteboxcodes.byteboxbackend.service.DockerExecutionService;
import com.byteboxcodes.byteboxbackend.service.strategy.CodeExecutionStrategy;

@Service
public class DockerExecutionImpl implements DockerExecutionService {

    private final Map<String, CodeExecutionStrategy> strategies;

    public DockerExecutionImpl(List<CodeExecutionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getLanguageName().toUpperCase(),
                        strategy -> strategy
                ));
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

        return strategy.execute(code, input);
    }
}
