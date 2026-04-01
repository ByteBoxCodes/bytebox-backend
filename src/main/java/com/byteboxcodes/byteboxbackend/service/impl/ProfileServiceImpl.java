package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.HeaderProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.HeatMapDTO;
import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;
import com.byteboxcodes.byteboxbackend.dto.PublicUserProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.SubmissionSummaryDTO;
import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

        private final SubmissionRepository submissionRepository;
        private final UserRepository userRepository;
        private final ProblemRespository problemRespository;

        // ─────────────────────────────────────────────────────────────────────────
        // PUBLIC COMBINED PROFILE  —  GET /api/profile/{username}
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public PublicUserProfileResponse getPublicProfile(String username) {

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found: " + username));

                UUID userId = user.getId();

                // ── Stats queries ─────────────────────────────────────────────────────
                long totalSubmissions = submissionRepository.countByUser_Id(userId);
                long acceptedSubmissions = submissionRepository.countByUser_IdAndStatus(userId,
                                SubmissionStatus.ACCEPTED);
                long totalSolved = submissionRepository.countSolvedProblems(userId);

                // Difficulty breakdown — total active problems
                long totalEasy = 0, totalMedium = 0, totalHard = 0;
                for (Object[] row : problemRespository.countActiveProblemsGroupedByDifficulty()) {
                        Difficulty d = (Difficulty) row[0];
                        long count = (Long) row[1];
                        switch (d) {
                                case EASY -> totalEasy = count;
                                case MEDIUM -> totalMedium = count;
                                case HARD -> totalHard = count;
                        }
                }

                // Difficulty breakdown — user solved
                long easySolved = 0, mediumSolved = 0, hardSolved = 0;
                for (Object[] row : submissionRepository.countSolvedByDifficulty(userId)) {
                        Difficulty d = (Difficulty) row[0];
                        long count = (Long) row[1];
                        switch (d) {
                                case EASY -> easySolved = count;
                                case MEDIUM -> mediumSolved = count;
                                case HARD -> hardSolved = count;
                        }
                }

                // Heatmap
                List<HeatMapDTO> heatmap = submissionRepository.getHeatmapData(userId).stream()
                                .map(row -> new HeatMapDTO((LocalDate) row[0], (Long) row[1]))
                                .toList();

                int currentStreak = user.getEffectiveCurrentStreak();
                int maxStreak = user.getMaxStreak();
                List<String> languages = submissionRepository.findDistinctLanguagesByUserId(userId);
                double acceptanceRate = totalSubmissions == 0
                                ? 0
                                : Math.round(((double) acceptedSubmissions / totalSubmissions) * 100);

                // ── Recent 5 submissions (lightweight — no code body) ─────────────────
                List<SubmissionSummaryDTO> recentSubmissions = submissionRepository
                                .findTop5ByUser_IdOrderBySubmittedAtDesc(userId)
                                .stream()
                                .map(SubmissionSummaryDTO::fromEntity)
                                .toList();

                // ── Member since ──────────────────────────────────────────────────────
                String memberSince = user.getCreatedAt() != null
                                ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                                : "Unknown";

                return PublicUserProfileResponse.builder()
                                // --- profile details ---
                                .username(user.getUsername())
                                .name(user.getName())
                                .bio(user.getBio())
                                .avatarUrl(user.getAvatarUrl())
                                .level(user.getLevel())
                                .points(user.getPoints())
                                .levelXp(user.getLevelXp())
                                .preferredLanguage(user.getPreferredLanguage())
                                .githubUsername(user.getGithubUsername())
                                .linkedinUsername(user.getLinkedinUsername())
                                .twitterUsername(user.getTwitterUsername())
                                .instagramUsername(user.getInstagramUsername())
                                .websiteUrl(user.getWebsiteUrl())
                                .memberSince(memberSince)
                                // --- stats ---
                                .totalSolved(totalSolved)
                                .easySolved(easySolved)
                                .mediumSolved(mediumSolved)
                                .hardSolved(hardSolved)
                                .totalEasy(totalEasy)
                                .totalMedium(totalMedium)
                                .totalHard(totalHard)
                                .totalSubmissions(totalSubmissions)
                                .acceptedSubmissions(acceptedSubmissions)
                                .acceptanceRate(acceptanceRate)
                                .currentStreak(currentStreak)
                                .maxStreak(maxStreak)
                                .languages(languages)
                                .heatmap(heatmap)
                                // --- recent submissions ---
                                .recentSubmissions(recentSubmissions)
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // EXISTING ENDPOINTS (unchanged)
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public ProfileStatsResponse getProfileStats() {

                String email = SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                UUID userId = user.getId();

                long totalSubmissions = submissionRepository.countByUser_Id(userId);

                long acceptedSubmissions = submissionRepository.countByUser_IdAndStatus(
                                userId,
                                SubmissionStatus.ACCEPTED);

                long totalSolved = submissionRepository.countSolvedProblems(userId);

                long totalProblems = problemRespository.countByIsActiveTrue();

                List<Object[]> totalDifficultyStats = problemRespository.countActiveProblemsGroupedByDifficulty();

                long totalEasy = 0, totalMedium = 0, totalHard = 0;

                for (Object[] row : totalDifficultyStats) {
                        Difficulty difficulty = (Difficulty) row[0];
                        long count = (Long) row[1];

                        switch (difficulty) {
                                case EASY -> totalEasy = count;
                                case MEDIUM -> totalMedium = count;
                                case HARD -> totalHard = count;
                        }
                }

                List<Object[]> difficultyStats = submissionRepository.countSolvedByDifficulty(userId);

                long easy = 0, medium = 0, hard = 0;

                for (Object[] row : difficultyStats) {
                        Difficulty difficulty = (Difficulty) row[0];
                        long count = (Long) row[1];

                        switch (difficulty) {
                                case EASY -> easy = count;
                                case MEDIUM -> medium = count;
                                case HARD -> hard = count;
                        }
                }

                List<Object[]> heatmapRaw = submissionRepository.getHeatmapData(userId);

                List<HeatMapDTO> heatmap = heatmapRaw.stream()
                                .map(row -> new HeatMapDTO(
                                                (LocalDate) row[0],
                                                (Long) row[1]))
                                .toList();

                int streak = user.getEffectiveCurrentStreak();
                int maxStreak = user.getMaxStreak();
                List<String> languages = submissionRepository.findDistinctLanguagesByUserId(userId);

                double acceptanceRate = totalSubmissions == 0
                                ? 0
                                : ((double) acceptedSubmissions / totalSubmissions) * 100;

                return ProfileStatsResponse.builder()
                                .totalSubmissions(totalSubmissions)
                                .acceptedSubmissions(acceptedSubmissions)
                                .totalProblems(totalProblems)
                                .totalSolvedProblems(totalSolved)
                                .easySolved(easy)
                                .totalEasy(totalEasy)
                                .mediumSolved(medium)
                                .totalMedium(totalMedium)
                                .hardSolved(hard)
                                .totalHard(totalHard)
                                .acceptanceRate(Math.round(acceptanceRate))
                                .currentStreak(streak)
                                .maxStreak(maxStreak)
                                .languages(languages)
                                .heatmap(heatmap)
                                .build();
        }

        @Override
        public HeaderProfileResponse getHeaderDetails() {

                String email = (String) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return HeaderProfileResponse.builder()
                                .username(user.getUsername())
                                .name(user.getName())
                                .avatarUrl(user.getAvatarUrl())
                                .level(user.getLevel())
                                .points(user.getPoints())
                                .levelXp(user.getLevelXp())
                                .currentStreak(user.getEffectiveCurrentStreak())
                                .maxStreak(user.getMaxStreak())
                                .build();
        }

}
