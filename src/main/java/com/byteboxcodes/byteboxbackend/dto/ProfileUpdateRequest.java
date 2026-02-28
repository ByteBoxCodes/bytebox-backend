package com.byteboxcodes.byteboxbackend.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String bio;
    private String avatarUrl;
    private String githubUsername;
    private String linkedinUsername;
    private String twitterUsername;
    private String websiteUrl;
}