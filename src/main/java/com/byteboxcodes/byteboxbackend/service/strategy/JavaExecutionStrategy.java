package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class JavaExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        return "eclipse-temurin:21-jdk";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "echo '" + base64Code + "' | base64 -d > Main.java && javac Main.java && java Main";
    }

    @Override
    public String getLanguageName() {
        return "JAVA";
    }
}
