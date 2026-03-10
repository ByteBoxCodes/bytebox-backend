package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.dto.SubmissionListResponse;
import com.byteboxcodes.byteboxbackend.dto.SubmissionRequest;
import com.byteboxcodes.byteboxbackend.dto.SubmissionResponse;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.service.JudgeService;
import com.byteboxcodes.byteboxbackend.service.SubmissionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SubmissionServiceImpl implements SubmissionService {

        private final SubmissionRepository submissionRepository;
        private final UserRepository userRepository;
        private final ProblemRespository problemRespository;
        private final JudgeService judgeService;

        private void validateKeywords(Problem problem, String code) {
                String requiredKeywords = problem.getRequiredKeywords();
                if (requiredKeywords == null || requiredKeywords.isBlank()) {
                        return;
                }
                String[] keywords = requiredKeywords.split(",");

                boolean matched = false;

                for (String keyword : keywords) {
                        if (code.contains(keyword.trim())) {
                                matched = true;
                                break;
                        }
                }

                if (!matched) {
                        throw new RuntimeException(
                                        "Write code according to problem statement,  Make sure you code contains ("
                                                        + requiredKeywords.toUpperCase() +
                                                        "), Check your code using required keywords, Check instructions & constraints properly");
                }

        }

        private void updateUserStreak(User user) {

                LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

                LocalDate lastDate = user.getLastAcceptedDate();

                if (lastDate == null) {
                        user.setCurrentStreak(1);
                } else {
                        long diff = ChronoUnit.DAYS.between(lastDate, today);

                        if (diff == 1) {
                                user.setCurrentStreak(user.getCurrentStreak() + 1);
                        } else if (diff > 1) {
                                user.setCurrentStreak(1);
                        }
                }

                if (user.getCurrentStreak() > user.getMaxStreak()) {
                        user.setMaxStreak(user.getCurrentStreak());
                }

                user.setLastAcceptedDate(today);
        }

        @Override
        public JudgeResult runCode(SubmissionRequest request) {

                Problem problem = problemRespository.findById(request.getProblemId())
                                .orElseThrow(() -> new RuntimeException("Problem not found"));

                validateKeywords(problem, request.getCode());

                return judgeService.judgeSample(request.getProblemId(), request.getCode(),
                                request.getLanguage());
        }

        // 🔥 Submit Solution (JWT-secured)
        @Override
        @Transactional
        public SubmissionResponse submitSolution(SubmissionRequest request) {

                // 1️⃣ Get authenticated user from JWT
                String email = (String) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // 2️⃣ Fetch problem
                Problem problem = problemRespository.findById(request.getProblemId())
                                .orElseThrow(() -> new RuntimeException("Problem not found"));

                validateKeywords(problem, request.getCode());

                JudgeResult judgeResult = judgeService.judge(
                                problem.getId(),
                                request.getCode(),
                                request.getLanguage());

                String judgeStatus = judgeResult.getStatus();

                // 3️⃣ Compile/Runtime errors — return immediately, don't save submission
                if ("COMPILE_ERROR".equals(judgeStatus) || "RUNTIME_ERROR".equals(judgeStatus)) {
                        return SubmissionResponse.builder()
                                        .status(SubmissionStatus.valueOf(judgeStatus))
                                        .passedTestCases(judgeResult.getPassedTestCases())
                                        .totalTestCases(judgeResult.getTotalTestCases())
                                        .errorMessage(judgeResult.getErrorMessage())
                                        .build();
                }

                boolean accepted = "ACCEPTED".equals(judgeStatus);
                SubmissionStatus status = accepted
                                ? SubmissionStatus.ACCEPTED
                                : SubmissionStatus.WRONG_ANSWER;

                // 4️⃣ Create submission entity
                Submission submission = Submission.builder()
                                .user(user)
                                .problem(problem)
                                .code(request.getCode())
                                .language(request.getLanguage())
                                .status(status)
                                .totalTestCases(judgeResult.getTotalTestCases())
                                .passedTestCases(judgeResult.getPassedTestCases())
                                .submittedAt(LocalDateTime.now())
                                .build();

                // 5️⃣ Save and return
                submissionRepository.save(submission);

                if (accepted) {
                        updateUserStreak(user);

                        // Award points if this is the user's first time solving this problem
                        // The user solved it just now, but if the count of accepted submissions for
                        // this problem is exactly 1 (the one we just saved)

                        long acceptedCount = submissionRepository
                                        .findByUserIdAndProblemId(user.getId(), problem.getId())
                                        .stream()
                                        .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                                        .count();

                        if (acceptedCount == 1) {
                                int pointsToAward = 0;
                                switch (problem.getDifficulty()) {
                                        case EASY:
                                                pointsToAward = 10;
                                                break;
                                        case MEDIUM:
                                                pointsToAward = 15;
                                                break;
                                        case HARD:
                                                pointsToAward = 20;
                                                break;
                                }
                                user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + pointsToAward);

                                // Check for topic completion bonus
                                if (problem.getTopic() != null) {
                                        long topicId = problem.getTopic().getId();
                                        long totalActiveProblemsInTopic = problemRespository
                                                        .countByTopicIdAndIsActiveTrue(topicId);
                                        long solvedProblemsInTopic = submissionRepository
                                                        .countSolvedProblemsByTopicId(user.getId(), topicId);

                                        if (totalActiveProblemsInTopic > 0
                                                        && solvedProblemsInTopic == totalActiveProblemsInTopic) {
                                                user.setPoints(user.getPoints() + 50);
                                        }
                                }

                                // Update Level: Every 15 points = 1 level
                                int newLevel = (user.getPoints() / 15) + 1;
                                user.setLevel(newLevel);

                                userRepository.save(user); // Save updated streak and points
                        } else {
                                userRepository.save(user); // Save updated streak
                        }
                }

                return SubmissionResponse.builder().submissionId(submission.getId()).status(status)
                                .passedTestCases(judgeResult.getPassedTestCases())
                                .totalTestCases(judgeResult.getTotalTestCases()).testCases(judgeResult.getTestCases())
                                .build();

        }

        // 🔥 Get submissions of logged-in user
        @Override
        public List<SubmissionListResponse> getSubmissionsByUser() {

                String email = (String) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return submissionRepository.findByUser(user)
                                .stream()
                                .map(SubmissionListResponse::fromEntity)
                                .collect(Collectors.toList());
        }

        // 🔥 Get submissions by problem
        @Override
        public List<SubmissionListResponse> getSubmissionsByProblem(UUID problemId) {

                Problem problem = problemRespository.findById(problemId)
                                .orElseThrow(() -> new RuntimeException("Problem not found"));

                return submissionRepository.findByProblem(problem)
                                .stream()
                                .map(SubmissionListResponse::fromEntity)
                                .collect(Collectors.toList());
        }

        @Override
        public List<SubmissionListResponse> getMySubmissionsByProblemId(UUID problemId) {

                String email = (String) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Problem problem = problemRespository.findById(problemId)
                                .orElseThrow(() -> new RuntimeException("Problem not found"));

                return submissionRepository.findByUserIdAndProblemId(user.getId(), problem.getId())
                                .stream()
                                .map(SubmissionListResponse::fromEntity)
                                .collect(Collectors.toList());
        }
}