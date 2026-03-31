package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class CExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        return "gcc:latest";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "echo '" + base64Code + "' | base64 -d > main.c && gcc main.c -o main && ./main";
    }

    @Override
    public String getLanguageName() {
        return "C";
    }
}
