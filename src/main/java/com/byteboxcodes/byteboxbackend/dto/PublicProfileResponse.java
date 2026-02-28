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

    private String githubUrl;
    private String linkedinUrl;
    private String twitterUrl;
    private String websiteUrl;
    private String createdAt;
}