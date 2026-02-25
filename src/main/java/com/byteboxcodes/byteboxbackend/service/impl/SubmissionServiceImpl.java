package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
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
                        throw new RuntimeException("Write code according to problem statement use instructions");
                }

        }

        @Override
        public JudgeResult runCode(SubmissionRequest request) {

                Problem problem = problemRespository.findById(request.getProblemId())
                                .orElseThrow(() -> new RuntimeException("Problem not found"));

                validateKeywords(problem, request.getCode());

                return judgeService.judgeSample(request.getProblemId(), request.getCode(), request.getLanguage());
        }

        // 🔥 Submit Solution (JWT-secured)
        @Override
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

                JudgeResult judgeResult = judgeService.judge(
                                problem.getId(),
                                request.getCode(),
                                request.getLanguage());

                SubmissionStatus status = judgeResult.isAccepted()
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

                return SubmissionResponse.builder()
                                .submissionId(submission.getId())
                                .status(status)
                                .totalTestCases(judgeResult.getTotalTestCases())
                                .passedTestCases(judgeResult.getPassedTestCases())
                                .build();
        }

        // 🔥 Get submissions of logged-in user
        @Override
        public List<Submission> getSubmissionsByUser() {

                String email = (String) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return submissionRepository.findByUser(user);
        }

        // 🔥 Get submissions by problem
        @Override
        public List<Submission> getSubmissionsByProblem(UUID problemId) {

                Problem problem = problemRespository.findById(problemId)
                                .orElseThrow(() -> new RuntimeException("Problem not found"));

                return submissionRepository.findByProblem(problem);
        }
}