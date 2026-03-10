package com.byteboxcodes.byteboxbackend.dto;

import com.byteboxcodes.byteboxbackend.entity.ProgrammingLanguage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicProfileResponse {

    private String username;
    private String name;
    private String bio;
    private String avatarUrl;
    private ProgrammingLanguage preferredLanguage;
    private Integer points;
    private Integer level;
    private Long totalProblemsolved;

    private String githubUsername;
    private String linkedinUsername;
    private String twitterUsername;
    private String websiteUrl;
    private String instagramUsername;
    private String createdAt;

}