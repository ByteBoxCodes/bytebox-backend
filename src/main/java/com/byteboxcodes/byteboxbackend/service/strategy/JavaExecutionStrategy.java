package com.byteboxcodes.byteboxbackend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class JavaExecutionStrategy extends AbstractDockerExecutionStrategy {

    @Override
    public String getDockerImage() {
        // Alpine variant of eclipse-temurin isn't supported on ARM64 Macs
        return "eclipse-temurin:17-jdk";
    }

    @Override
    protected String getExecutionCommand(String base64Code) {
        return "trap 'rm -rf /tmp/work /tmp/hsperfdata_* /tmp/*.log' EXIT; mkdir -p /tmp/work && printf '%s' \""
                + base64Code + "\""
                + " | base64 -d > /tmp/work/Main.java"
                + " && javac /tmp/work/Main.java -d /tmp/work"
                + " && java"
                + " -Xms32m -Xmx96m" // pre-allocate heap — avoids resize pauses
                + " -XX:+UseSerialGC" // fastest GC for short single-threaded runs
                + " -XX:+TieredCompilation" // enable tiered compilation
                + " -XX:TieredStopAtLevel=1" // skip C2 optimizer — much faster JVM startup
                + " -XX:-UsePerfData" // don't write perf files to /tmp/hsperfdata_*
                + " -XX:+UseCompressedOops" // smaller memory footprint
                + " -cp /tmp/work Main";
    }

    @Override
    public String getLanguageName() {
        return "JAVA";
    }
}
