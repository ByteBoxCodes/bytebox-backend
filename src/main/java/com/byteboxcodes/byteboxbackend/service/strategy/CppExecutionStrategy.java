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
        return "echo '" + base64Code + "' | base64 -d > /tmp/main.cpp"
                + " && g++ -O2 /tmp/main.cpp -o /tmp/main && /tmp/main"
                + " ; rm -f /tmp/main.cpp /tmp/main 2>/dev/null";
    }

    @Override
    public String getLanguageName() {
        return "C++";
    }
}
