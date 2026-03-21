package com.byteboxcodes.byteboxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponse {
    private String username;
    private String name;
    private String avatarUrl;
    private Integer points;
    private Integer level;
    private Integer levelXp;
    private Long totalProblemsolved;
}
