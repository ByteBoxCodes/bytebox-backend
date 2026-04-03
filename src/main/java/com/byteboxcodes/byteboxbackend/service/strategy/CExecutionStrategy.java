package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class CExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        // Official GCC image (Debian-based) — no Alpine variant exists on Docker Hub
        return "gcc:latest";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "echo '" + base64Code + "' | base64 -d > /tmp/main.c"
                + " && gcc -O2 /tmp/main.c -o /tmp/main && /tmp/main"
                + " ; rm -f /tmp/main.c /tmp/main 2>/dev/null";
    }

    @Override
    public String getLanguageName() {
        return "C";
    }
}
