package com.byteboxcodes.byteboxbackend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "bio", columnDefinition = "TEXT", length = 150)
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "github_username")
    private String githubUsername;

    @Column(name = "linkedin_username")
    private String linkedinUsername;

    @Column(name = "twitter_username")
    private String twitterUsername;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "instagram_username")
    private String instagramUsername;

    @Builder.Default
    @Column(name = "points", columnDefinition = "integer default 0")
    private Integer points = 0;

    @Builder.Default
    @Column(name = "level", columnDefinition = "integer default 1")
    private Integer level = 1;

    @Builder.Default
    @Column(name = "level_xp", columnDefinition = "integer default 0")
    private Integer levelXp = 0;

    @Builder.Default
    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(name = "max_streak")
    private Integer maxStreak = 0;

    @Column(name = "last_accepted_date")
    private LocalDate lastAcceptedDate;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language")
    private ProgrammingLanguage preferredLanguage = ProgrammingLanguage.CPP;

    private LocalDateTime createdAt;

    public Integer getCurrentStreak() {
        return currentStreak == null ? 0 : currentStreak;
    }

    public Integer getEffectiveCurrentStreak() {
        if (currentStreak == null || currentStreak == 0)
            return 0;
        if (lastAcceptedDate == null)
            return 0;

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        long diff = ChronoUnit.DAYS.between(lastAcceptedDate, today);

        if (diff > 1) {
            return 0;
        }
        return currentStreak;
    }

    public Integer getMaxStreak() {
        return maxStreak == null ? 0 : maxStreak;
    }
}
