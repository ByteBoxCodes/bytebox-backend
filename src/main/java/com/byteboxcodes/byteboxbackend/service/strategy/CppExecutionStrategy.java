package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class CppExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        // Official GCC image (Debian-based) — no Alpine variant exists on Docker Hub
        return "gcc:latest";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        // Decode to /tmp (RAM), compile output also to /tmp, execute from /tmp
        return "echo '" + base64Code + "' | base64 -d > /tmp/main.cpp && g++ /tmp/main.cpp -o /tmp/main && /tmp/main";
    }

    @Override
    public String getLanguageName() {
        return "C++";
    }
}
