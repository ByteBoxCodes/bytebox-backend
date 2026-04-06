package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class CExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    public String getDockerImage() {
        // Official GCC image (Debian-based) — no Alpine variant exists on Docker Hub
        return "gcc:latest";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "trap 'rm -f /tmp/main.c /tmp/main' EXIT; printf '%s' \"" + base64Code + "\" | base64 -d > /tmp/main.c"
                + " && gcc -O2 /tmp/main.c -o /tmp/main && /tmp/main";
    }

    @Override
    public String getLanguageName() {
        return "C";
    }
}
