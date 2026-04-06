package com.byteboxcodes.byteboxbackend.service.strategy;

import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;

public interface CodeExecutionStrategy {
    DockerExecutionResult execute(String code, String input, String containerId);
    String getLanguageName();
    String getDockerImage();
}
