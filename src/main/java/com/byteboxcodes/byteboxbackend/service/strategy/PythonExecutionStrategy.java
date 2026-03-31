package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class PythonExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        return "python:3.11-slim";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "echo '" + base64Code + "' | base64 -d > main.py && python3 main.py";
    }

    @Override
    public String getLanguageName() {
        return "PYTHON";
    }
}
