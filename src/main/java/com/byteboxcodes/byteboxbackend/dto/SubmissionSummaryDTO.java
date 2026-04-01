package com.byteboxcodes.byteboxbackend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionSummaryDTO {

    private UUID id;
    private UUID problemId;
    private String problemTitle;
    private Difficulty problemDifficulty;
    private String language;
    private SubmissionStatus status;
    private int passedTestCases;
    private int totalTestCases;
    private LocalDateTime submittedAt;

    public static SubmissionSummaryDTO fromEntity(Submission submission) {
        return SubmissionSummaryDTO.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .problemDifficulty(submission.getProblem().getDifficulty())
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .passedTestCases(submission.getPassedTestCases())
                .totalTestCases(submission.getTotalTestCases())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
