package com.byteboxcodes.byteboxbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class DockerExecutionConfig {

    @Value("${docker.execution.pool-size:2}")
    private int poolSizePerLanguage;

    @Value("${docker.execution.max-concurrent:4}")
    private int maxConcurrentExecutions;

    @Value("${docker.execution.timeout-seconds:10}")
    private int timeoutSeconds;

    @Value("${docker.execution.queue-capacity:100}")
    private int queueCapacity;

}
