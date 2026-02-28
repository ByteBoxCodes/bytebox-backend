package com.byteboxcodes.byteboxbackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String bio;

    private String name;

    @JsonAlias({ "avatar", "avatarUrl" })
    private String avatarUrl;

    @JsonAlias({ "github", "githubUsername" })
    private String githubUsername;

    @JsonAlias({ "linkedin", "linkedinUsername" })
    private String linkedinUsername;

    @JsonAlias({ "twitter", "twitterUsername" })
    private String twitterUsername;

    @JsonAlias({ "instagram", "instagramUsername" })
    private String instagramUsername;

    @JsonAlias({ "website", "websiteUrl" })
    private String websiteUrl;
}