package com.byteboxcodes.byteboxbackend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JudgeResult {

    private String status; // ACCEPTED, WRONG_ANSWER, COMPILE_ERROR, RUNTIME_ERROR
    private int passedTestCases;
    private int totalTestCases;

    private String errorMessage; // only for COMPILE_ERROR / RUNTIME_ERROR

    private List<TestCaseResult> testCases;

    public static JudgeResult success(int total, int passed, List<TestCaseResult> testCases) {
        return JudgeResult.builder()
                .status("ACCEPTED")
                .totalTestCases(total)
                .passedTestCases(passed)
                .testCases(testCases)
                .build();
    }

    public static JudgeResult wrongAnswer(int total, int passed, List<TestCaseResult> testCases) {
        return JudgeResult.builder()
                .status("WRONG_ANSWER")
                .totalTestCases(total)
                .passedTestCases(passed)
                .testCases(testCases)
                .build();
    }

    public static JudgeResult compileError(String message, int total, int passed) {
        return JudgeResult.builder()
                .status("COMPILE_ERROR")
                .totalTestCases(total)
                .passedTestCases(passed)
                .errorMessage(message)
                .build();
    }

    public static JudgeResult runtimeError(String message, int total, int passed) {
        return JudgeResult.builder()
                .status("RUNTIME_ERROR")
                .totalTestCases(total)
                .passedTestCases(passed)
                .errorMessage(message)
                .build();
    }
}