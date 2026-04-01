package com.byteboxcodes.byteboxbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byteboxcodes.byteboxbackend.dto.ApiResponse;
import com.byteboxcodes.byteboxbackend.dto.HeaderProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;
import com.byteboxcodes.byteboxbackend.dto.ProfileUpdateRequest;
import com.byteboxcodes.byteboxbackend.dto.PublicUserProfileResponse;
import com.byteboxcodes.byteboxbackend.service.FileStorageService;
import com.byteboxcodes.byteboxbackend.service.ProfileService;
import com.byteboxcodes.byteboxbackend.service.UserService;
import com.byteboxcodes.byteboxbackend.dto.PublicProfileResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final FileStorageService fileStorageService;

    @PutMapping("/update")
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

    @GetMapping("/header")
    public ApiResponse<HeaderProfileResponse> getHeaderDetails() {
        HeaderProfileResponse headerDetails = profileService.getHeaderDetails();
        return ApiResponse.<HeaderProfileResponse>builder()
                .success(true)
                .message("Header details fetched successfully")
                .data(headerDetails)
                .build();
    }

    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) {

        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image allowed");
        }

        if (file.getSize() > 1024 * 1024 * 2) {
            throw new RuntimeException("File size must be less than 2MB");
        }

        String avatarUrl = fileStorageService.uploadFile(file);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setAvatarUrl(avatarUrl);
        userService.updateProfile(request);

        return avatarUrl;
    }
    @GetMapping("/{username}")
    public ApiResponse<PublicUserProfileResponse> getPublicProfile(@PathVariable String username) {
        PublicUserProfileResponse profile = profileService.getPublicProfile(username);
        return ApiResponse.<PublicUserProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(profile)
                .build();
    }
}
