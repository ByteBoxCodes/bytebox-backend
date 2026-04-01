package com.byteboxcodes.byteboxbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.ApiResponse;
import com.byteboxcodes.byteboxbackend.dto.PlatformResponse;
import com.byteboxcodes.byteboxbackend.service.PlatformService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping("/stats")
    public ApiResponse<PlatformResponse> getPlatformStats() {
        PlatformResponse stats = platformService.getPlatformStats();
        
        return ApiResponse.<PlatformResponse>builder()
                .success(true)
                .message("Platform stats fetched successfully")
                .data(stats)
                .build();
    }
}
