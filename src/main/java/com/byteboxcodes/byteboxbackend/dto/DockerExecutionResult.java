package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DockerExecutionResult {
    private String stdout;
    private String stderr;
    private int exitCode;
    private boolean isTimeout;
}
