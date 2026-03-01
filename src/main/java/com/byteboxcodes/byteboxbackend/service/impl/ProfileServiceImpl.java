package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.HeatMapDTO;
import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;
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

    // Calculating the streak of the user
    private int calculateStreak(List<LocalDate> dates) {

        if (dates.isEmpty())
            return 0;

        Set<LocalDate> dateSet = new HashSet<>(dates);

        LocalDate today = LocalDate.now();
        int streak = 0;

        while (dateSet.contains(today.minusDays(streak))) {
            streak++;
        }

        return streak;
    }

    private int calculateMaxStreak(List<LocalDate> dates) {

        if (dates.isEmpty())
            return 0;

        Set<LocalDate> dateSet = new HashSet<>(dates);

        LocalDate today = LocalDate.now();
        int maxStreak = 0;
        int currentStreak = 0;

        for (int i = 0; i < 365; i++) {
            LocalDate checkDate = today.minusDays(i);

            if (dateSet.contains(checkDate)) {
                currentStreak++;
            } else {
                maxStreak = Math.max(maxStreak, currentStreak);
                currentStreak = 0;
            }
        }

        maxStreak = Math.max(maxStreak, currentStreak);

        return maxStreak;
    }

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

        List<LocalDate> activeDates = heatmap.stream()
                .map(HeatMapDTO::getDate)
                .toList();

        int streak = calculateStreak(activeDates);

        int maxStreak = calculateMaxStreak(activeDates);

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

}
