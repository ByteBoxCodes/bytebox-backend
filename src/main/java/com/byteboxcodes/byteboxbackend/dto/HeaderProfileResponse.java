package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderProfileResponse {

    private String username;
    private String name;
    private String avatarUrl;
    private Integer points;
    private Integer level;
    private Integer levelXp;
    private Integer currentStreak;
    private Integer maxStreak;
    private boolean isPremium;

    private String activeTitle;
    private String activeAvatar;
    private String activeCard;
}