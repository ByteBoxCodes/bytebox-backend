package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopicResponse {

    private Long id;
    private String name;
    private String description;

    private Long totalProblems;
    private Long solvedProblems;

    public TopicResponse(Long id, String name, String description, Long totalProblems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalProblems = totalProblems;
        this.solvedProblems = 0L; // default
    }

    public TopicResponse(
            Long id,
            String name,
            String description,
            Long totalProblems,
            Long solvedProblems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalProblems = totalProblems;
        this.solvedProblems = solvedProblems;
    }
}