package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class JavaExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    protected String getDockerImage() {
        // Alpine-based Temurin JDK 21 — lighter than the Debian variant
        return "eclipse-temurin:21-jdk-alpine";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        // mkdir /tmp/work, decode Main.java into it (RAM), compile in /tmp, run from /tmp
        // -cp sets the classpath so the JVM finds the compiled Main.class
        return "mkdir -p /tmp/work && echo '" + base64Code
                + "' | base64 -d > /tmp/work/Main.java"
                + " && javac /tmp/work/Main.java -d /tmp/work"
                + " && java -cp /tmp/work Main";
    }

    @Override
    public String getLanguageName() {
        return "JAVA";
    }
}
