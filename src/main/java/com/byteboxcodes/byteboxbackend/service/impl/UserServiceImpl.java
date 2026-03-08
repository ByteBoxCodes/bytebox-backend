package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.LeaderboardResponse;
import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.ProfileUpdateRequest;
import com.byteboxcodes.byteboxbackend.dto.PublicProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.entity.TokenTypeEnum;
import com.byteboxcodes.byteboxbackend.entity.EmailVerification;
import com.byteboxcodes.byteboxbackend.entity.ProgrammingLanguage;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.exception.UserAlreadyExists;
import com.byteboxcodes.byteboxbackend.repository.EmailRespository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.security.JwtUtil;
import com.byteboxcodes.byteboxbackend.service.EmailService;
import com.byteboxcodes.byteboxbackend.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

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
    private final SubmissionRepository submissionRepository;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    @Transactional
    public void register(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExists("Email already exists");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExists("Username already exists");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new UserAlreadyExists("Password is required");
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
                .tokenType(TokenTypeEnum.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        emailVerificationRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);

    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if (user.getPassword() == null) {
            throw new RuntimeException("This account uses Google Sign-In. Please login with Google.");
        }

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
                .preferredLanguage(user.getPreferredLanguage())
                .points(user.getPoints())
                .totalProblemsolved(submissionRepository.countSolvedProblems(user.getId()))
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
        if (request.getUsername() != null) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
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

        if (verificationToken.getTokenType() != null
                && verificationToken.getTokenType() != TokenTypeEnum.EMAIL_VERIFICATION) {
            throw new RuntimeException("Invalid token type");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationRepository.delete(verificationToken);
    }

    @Override
    @Transactional
    public String loginWithGoogle(String idTokenString) {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }

        if (idToken == null) {
            throw new RuntimeException("Invalid Google token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Sync name & avatar from Google on every login
                    if (name != null)
                        existingUser.setName(name);
                    if (picture != null)
                        existingUser.setAvatarUrl(picture);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .username(generateUsername(email))
                            .name(name != null ? name : "User")
                            .avatarUrl(picture)
                            .role("USER")
                            .emailVerified(true)
                            .enabled(true)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                });

        return jwtUtil.generateToken(user.getEmail());
    }

    /**
     * Generates a unique username from the email prefix.
     * e.g. "mohit@gmail.com" → "mohit", or "mohit1" if "mohit" is taken.
     */
    private String generateUsername(String email) {
        String base = email.split("@")[0]
                .replaceAll("[^a-zA-Z0-9_]", ""); // strip special chars
        String username = base;
        int suffix = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = base + suffix;
            suffix++;
        }
        return username;
    }

    @Override
    public void updatePreferredLanguage(ProgrammingLanguage language) {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPreferredLanguage(language);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword() == null) {
            throw new RuntimeException("This account uses Google Sign-In. You cannot reset password.");
        }

        String token = UUID.randomUUID().toString();

        EmailVerification passwordResetToken = EmailVerification.builder()
                .token(token)
                .user(user)
                .tokenType(TokenTypeEnum.PASSWORD_RESET)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        emailVerificationRepository.save(passwordResetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        EmailVerification resetToken = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getTokenType() != TokenTypeEnum.PASSWORD_RESET) {
            throw new RuntimeException("Invalid token type");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailVerificationRepository.delete(resetToken);
    }

    @Override
    public List<LeaderboardResponse> getLeaderboard() {
        return userRepository.getLeaderboard();
    }
}
