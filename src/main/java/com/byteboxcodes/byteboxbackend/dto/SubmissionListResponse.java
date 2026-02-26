package com.byteboxcodes.byteboxbackend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionListResponse {

    private UUID id;
    private UUID userId;
    private UUID problemId;
    private String problemTitle;
    private String code;
    private String language;
    private int totalTestCases;
    private int passedTestCases;
    private SubmissionStatus status;
    private LocalDateTime submittedAt;

    public static SubmissionListResponse fromEntity(Submission submission) {
        return SubmissionListResponse.builder()
                .id(submission.getId())
                .userId(submission.getUser().getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .code(submission.getCode())
                .language(submission.getLanguage())
                .totalTestCases(submission.getTotalTestCases())
                .passedTestCases(submission.getPassedTestCases())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
