package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class CppExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        return "gcc:latest";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "echo '" + base64Code + "' | base64 -d > main.cpp && g++ main.cpp -o main && ./main";
    }

    @Override
    public String getLanguageName() {
        return "C++"; // Or "CPP" depending on how it's sent from frontend
    }
}
