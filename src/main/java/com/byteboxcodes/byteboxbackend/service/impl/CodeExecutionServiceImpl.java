package com.byteboxcodes.byteboxbackend.service.impl;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.service.CodeExecutionService;

@Service
public class CodeExecutionServiceImpl implements CodeExecutionService {

    @Override
    public String execute(String code, String language, String input) {

        // ⚠️ Temporary simulation logic
        // Replace this later with real execution engine

        if (code.contains("return")) {
            return input; // Fake success
        }

        return "WRONG";
    }
}