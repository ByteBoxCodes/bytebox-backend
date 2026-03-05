package com.byteboxcodes.byteboxbackend.dto;

import java.util.List;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionResponse {

    private UUID submissionId;
    private SubmissionStatus status;
    private int passedTestCases;
    private int totalTestCases;

    private String errorMessage;

    private List<TestCaseResult> testCases;
}