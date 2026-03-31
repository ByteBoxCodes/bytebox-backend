package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;

public interface DockerExecutionService {

    DockerExecutionResult runCode(String language, String code, String input);

}
