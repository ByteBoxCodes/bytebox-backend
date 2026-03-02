package com.byteboxcodes.byteboxbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.ApiResponse;
import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.dto.VerifyRequest;
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
    public ApiResponse<String> verifyEmail(@RequestBody VerifyRequest request) {
        userService.verifyEmail(request.getToken());
        return ApiResponse.<String>builder()
                .success(true)
                .message("Email verified successfully")
                .data("Email verified successfully")
                .build();
    }

}
