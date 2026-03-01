package com.byteboxcodes.byteboxbackend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileStatsResponse {

    private long totalSubmissions;
    private long acceptedSubmissions;

    private long totalProblems;
    private long totalSolvedProblems;

    private int currentStreak;
    private int maxStreak;
    private List<HeatMapDTO> heatmap;

    private long easySolved;
    private long totalEasy;
    private long mediumSolved;
    private long totalMedium;
    private long hardSolved;
    private long totalHard;

    private List<String> languages;

    private double acceptanceRate;
}
