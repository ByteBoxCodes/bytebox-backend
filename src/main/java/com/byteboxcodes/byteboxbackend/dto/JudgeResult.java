package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JudgeResult {

    private boolean accepted;
    private int totalTestCases;
    private int passedTestCases;

    private String errorType;
    private String errorMessage;

    public static JudgeResult success(int total, int passed) {
        return JudgeResult.builder()
                .accepted(true)
                .totalTestCases(total)
                .passedTestCases(passed)
                .build();
    }

    public static JudgeResult wrongAnswer(int total, int passed) {
        return JudgeResult.builder()
                .accepted(false)
                .totalTestCases(total)
                .passedTestCases(passed)
                .errorType("WRONG_ANSWER")
                .errorMessage("Wrong Answer")
                .build();
    }

    public static JudgeResult compileError(String message, int total, int passed) {
        return JudgeResult.builder()
                .accepted(false)
                .totalTestCases(total)
                .passedTestCases(passed)
                .errorType("COMPILE_ERROR")
                .errorMessage(message)
                .build();
    }

    public static JudgeResult runtimeError(String message, int total, int passed) {
        return JudgeResult.builder()
                .accepted(false)
                .totalTestCases(total)
                .passedTestCases(passed)
                .errorType("RUNTIME_ERROR")
                .errorMessage(message)
                .build();
    }
}