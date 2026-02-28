package com.byteboxcodes.byteboxbackend.dto;

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

    private String githubUsername;
    private String linkedinUsername;
    private String twitterUsername;
    private String websiteUrl;
    private String instagramUsername;
    private String createdAt;
}