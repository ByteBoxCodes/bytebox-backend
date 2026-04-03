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
    private Integer levelXp;
    private Long totalProblemsolved;
    private boolean isPremium;

    private String activeTitle;
    private String activeAvatar;
    private String activeCard;

    private String githubUsername;
    private String linkedinUsername;
    private String twitterUsername;
    private String websiteUrl;
    private String instagramUsername;
    private String createdAt;

}