package com.byteboxcodes.byteboxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatformResponse {
    private long totalUsers;
    private long totalProblems;
    private long totalTopics;
}
