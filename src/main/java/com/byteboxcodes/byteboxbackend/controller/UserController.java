package com.byteboxcodes.byteboxbackend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.ApiResponse;
import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;
import com.byteboxcodes.byteboxbackend.dto.ProfileUpdateRequest;
import com.byteboxcodes.byteboxbackend.dto.PublicProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.dto.UserResponse;
import com.byteboxcodes.byteboxbackend.service.ProfileService;
import com.byteboxcodes.byteboxbackend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserRequest request) {
        userService.register(request);
        return ApiResponse.<String>builder()
                .success(true)
                .message("User registered successfully")
                .data("User registered successfully")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);

        if (token != null) {
            return ApiResponse.<String>builder()
                    .success(true)
                    .message("Login successful")
                    .data(token)
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .success(false)
                    .message("Login failed")
                    .data("Invalid credentials")
                    .build();
        }
    }

    @PutMapping("/profile")
    public ApiResponse<String> updateProfile(@RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(request);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Profile updated successfully")
                .data("Profile updated successfully")
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<PublicProfileResponse> getCurrentUser() {
        PublicProfileResponse user = userService.getCurrentUser();
        return ApiResponse.<PublicProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(user)
                .build();
    }

    @GetMapping("/me/stats")
    public ApiResponse<ProfileStatsResponse> getProfileStats() {
        ProfileStatsResponse stats = profileService.getProfileStats();
        return ApiResponse.<ProfileStatsResponse>builder()
                .success(true)
                .message("Profile stats fetched successfully")
                .data(stats)
                .build();
    }

}
