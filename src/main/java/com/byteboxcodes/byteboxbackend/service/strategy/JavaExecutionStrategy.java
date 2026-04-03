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
        return "mkdir -p /tmp/work && echo '" + base64Code
                + "' | base64 -d > /tmp/work/Main.java"
                + " && javac /tmp/work/Main.java -d /tmp/work"
                + " && java"
                + " -Xms64m -Xmx128m"             // pre-allocate heap — avoids resize pauses
                + " -XX:+UseSerialGC"              // fastest GC for short single-threaded runs
                + " -XX:+TieredCompilation"        // enable tiered compilation
                + " -XX:TieredStopAtLevel=1"       // skip C2 optimizer — much faster JVM startup
                + " -XX:-UsePerfData"              // don't write perf files to /tmp/hsperfdata_*
                + " -XX:+UseCompressedOops"        // smaller memory footprint
                + " -cp /tmp/work Main"
                + " ; rm -rf /tmp/work /tmp/hsperfdata_* /tmp/*.log 2>/dev/null"; // nuke ALL temp files
    }

    @Override
    public String getLanguageName() {
        return "JAVA";
    }
}
