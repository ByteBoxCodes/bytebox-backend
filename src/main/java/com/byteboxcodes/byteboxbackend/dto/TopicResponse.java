package com.byteboxcodes.byteboxbackend.dto;

import lombok.Getter;

@Getter
public class TopicResponse {

    private Long id;
    private String name;
    private String description;
    private Long problemsCount;

    public TopicResponse(Long id, String name, String description, Long problemsCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.problemsCount = problemsCount;
    }
}