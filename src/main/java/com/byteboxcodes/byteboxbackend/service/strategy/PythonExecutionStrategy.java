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
        return "echo '" + base64Code + "' | base64 -d > /tmp/main.py"
                + " && PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1 python3 -B /tmp/main.py"
                + " ; rm -f /tmp/main.py 2>/dev/null";
    }

    @Override
    public String getLanguageName() {
        return "PYTHON";
    }
}
