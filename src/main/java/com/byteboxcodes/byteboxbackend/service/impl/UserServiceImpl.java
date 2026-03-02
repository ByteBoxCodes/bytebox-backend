package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.ProfileUpdateRequest;
import com.byteboxcodes.byteboxbackend.dto.PublicProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.entity.EmailVerification;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.exception.UserAlreadyExists;
import com.byteboxcodes.byteboxbackend.repository.EmailRespository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.security.JwtUtil;
import com.byteboxcodes.byteboxbackend.service.EmailService;
import com.byteboxcodes.byteboxbackend.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailRespository emailVerificationRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void register(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExists("Email already exists");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExists("Username already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        EmailVerification emailVerificationToken = EmailVerification.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        emailVerificationRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);

    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Override
    public PublicProfileResponse getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return PublicProfileResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .githubUsername(user.getGithubUsername())
                .linkedinUsername(user.getLinkedinUsername())
                .twitterUsername(user.getTwitterUsername())
                .websiteUrl(user.getWebsiteUrl())
                .instagramUsername(user.getInstagramUsername())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(ProfileUpdateRequest request) {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getBio() != null)
            user.setBio(request.getBio());
        if (request.getName() != null)
            user.setName(request.getName());
        if (request.getAvatarUrl() != null)
            user.setAvatarUrl(request.getAvatarUrl());
        if (request.getGithubUsername() != null)
            user.setGithubUsername(request.getGithubUsername());
        if (request.getLinkedinUsername() != null)
            user.setLinkedinUsername(request.getLinkedinUsername());
        if (request.getTwitterUsername() != null)
            user.setTwitterUsername(request.getTwitterUsername());
        if (request.getWebsiteUrl() != null)
            user.setWebsiteUrl(request.getWebsiteUrl());
        if (request.getInstagramUsername() != null)
            user.setInstagramUsername(request.getInstagramUsername());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        EmailVerification verificationToken = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationRepository.delete(verificationToken);
    }

}
