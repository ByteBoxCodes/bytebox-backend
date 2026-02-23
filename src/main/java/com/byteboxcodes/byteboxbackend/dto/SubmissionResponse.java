package com.byteboxcodes.byteboxbackend.dto;

import java.util.UUID;

import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponse {

    private UUID submissionId;
    private SubmissionStatus status;
    private String totalTestCases;
    private String passedTestCases;
}