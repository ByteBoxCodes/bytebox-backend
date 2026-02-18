package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.SubmissionRequest;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.service.SubmissionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRespository problemRespository;

    @Override
    public Submission submitSolution(SubmissionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Problem problem = problemRespository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        SubmissionStatus status;
        if (request.getCode().contains("return")) {
            status = SubmissionStatus.ACCEPTED;
        } else {
            status = SubmissionStatus.WRONG_ANSWER;
        }

        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .code(request.getCode())
                .language(request.getLanguage())
                .status(status)
                .submittedAt(LocalDateTime.now())
                .build();

        return submissionRepository.save(submission);
    }

    @Override
    public List<Submission> getSubmissionsByUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));

        return submissionRepository.findByUser(user);
    }

    @Override
    public List<Submission> getSubmissionsByProblem(UUID problemId) {
        Problem problem = problemRespository.findById(problemId).orElseThrow(
                () -> new RuntimeException("Problem not found"));

        return submissionRepository.findByProblem(problem);
    }

}
