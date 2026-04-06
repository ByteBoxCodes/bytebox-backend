package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class CppExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    public String getDockerImage() {
        // Official GCC image (Debian-based) — no Alpine variant exists on Docker Hub
        return "gcc:latest";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "trap 'rm -f /tmp/main.cpp /tmp/main' EXIT; printf '%s' \"" + base64Code + "\" | base64 -d > /tmp/main.cpp"
                + " && g++ -O2 /tmp/main.cpp -o /tmp/main && /tmp/main";
    }

    @Override
    public String getLanguageName() {
        return "C++";
    }
}
