package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderProfileResponse {

    private String username;
    private String name;
    private String avatarUrl;
    private Integer currentStreak;
    private Integer maxStreak;
}