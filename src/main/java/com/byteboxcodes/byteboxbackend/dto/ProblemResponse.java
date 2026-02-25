package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder

public class ProblemResponse {

    private UUID id;
    private String title;
    private String description;
    private String difficulty;
    private String constraints;
    private String requiredKeywords;
    private Integer orderIndex;
    private Boolean isActive;
    private TopicResponse topic;
    private List<TestCaseResponse> sampleTestCases;
}