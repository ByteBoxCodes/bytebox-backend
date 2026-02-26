package com.byteboxcodes.byteboxbackend.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemListResponse {
    private UUID id;
    private String title;
    private String difficulty;
    private Integer orderIndex;
    private boolean isSolved;
}
