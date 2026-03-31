package com.byteboxcodes.byteboxbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.ApiResponse;
import com.byteboxcodes.byteboxbackend.dto.ForgotPasswordRequest;
import com.byteboxcodes.byteboxbackend.dto.GoogleAuthRequst;
import com.byteboxcodes.byteboxbackend.dto.LeaderboardResponse;
import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.PreferredLanguageRequest;
import com.byteboxcodes.byteboxbackend.dto.ResetPasswordRequest;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

    @PostMapping("/verify")
    public ApiResponse<String> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Email verified successfully")
                .data("Email verified successfully")
                .build();
    }

    @PostMapping("/google")
    public ApiResponse<String> googleAuth(@RequestBody GoogleAuthRequst googleAuthRequst) {
        String token = userService.loginWithGoogle(googleAuthRequst.getIdToken());
        return ApiResponse.<String>builder()
                .success(true)
                .message("Google auth successful")
                .data(token)
                .build();
    }

    @GetMapping("/leaderboard")
    public ApiResponse<List<LeaderboardResponse>> getLeaderboard() {
        return ApiResponse.<List<LeaderboardResponse>>builder()
                .success(true)
                .message("Leaderboard fetched successfully")
                .data(userService.getLeaderboard())
                .build();
    }

    @PutMapping("/preferred-language")
    public ApiResponse<String> updatePreferredLanguage(
            @RequestBody PreferredLanguageRequest request) {

        userService.updatePreferredLanguage(request.getPreferredLanguage());

        return ApiResponse.<String>builder()
                .success(true)
                .message("Preferred language updated")
                .data("Updated successfully")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ApiResponse.<String>builder()
                .success(true)
                .message("Password reset email sent")
                .data("Password reset email sent if associated with an account")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.<String>builder()
                .success(true)
                .message("Password updated successfully")
                .data("Password updated successfully")
                .build();
    }
}
