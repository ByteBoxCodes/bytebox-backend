package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class PythonExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        // Alpine variant — ~50MB vs 130MB for python:3.11-slim
        return "python:3.11-alpine";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        // Writes to /tmp (RAM-backed tmpfs), never touches the read-only container layer
        return "echo '" + base64Code + "' | base64 -d > /tmp/main.py && python3 /tmp/main.py";
    }

    @Override
    public String getLanguageName() {
        return "PYTHON";
    }
}
