package com.byteboxcodes.byteboxbackend.service.impl;

import com.byteboxcodes.byteboxbackend.service.CodeExecutionService;

// Commented out to prevent ApplicationContext failure when API properties are omitted
// @Service
public class Judge0ExecutionService implements CodeExecutionService {

    @Override
    public String execute(String code, String language, String input) {
        throw new RuntimeException("RapidAPI Engine is fully retired. Local Docker Execution is handling requests.");
    }
}