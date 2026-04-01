package com.byteboxcodes.byteboxbackend.dto;

import java.util.List;

import com.byteboxcodes.byteboxbackend.entity.ProgrammingLanguage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserProfileResponse {

    // ── Profile Details ───────────────────────────────────────────────────────
    private String username;
    private String name;
    private String bio;
    private String avatarUrl;
    private Integer level;
    private Integer points;
    private Integer levelXp;
    private ProgrammingLanguage preferredLanguage;
    private String githubUsername;
    private String linkedinUsername;
    private String twitterUsername;
    private String instagramUsername;
    private String websiteUrl;
    private String memberSince; // e.g. "March 2025"

    // ── Stats ─────────────────────────────────────────────────────────────────
    private long totalSolved;
    private long easySolved;
    private long mediumSolved;
    private long hardSolved;
    private long totalEasy;
    private long totalMedium;
    private long totalHard;
    private long totalSubmissions;
    private long acceptedSubmissions;
    private double acceptanceRate;
    private Integer currentStreak;
    private Integer maxStreak;
    private List<String> languages;
    private List<HeatMapDTO> heatmap;

    // ── Recent Submissions (last 15, no code) ────────────────────────────────
    private List<SubmissionSummaryDTO> recentSubmissions;
}
