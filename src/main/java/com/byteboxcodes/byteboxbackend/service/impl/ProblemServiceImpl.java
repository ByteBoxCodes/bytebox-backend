package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.ProblemListResponse;
import com.byteboxcodes.byteboxbackend.dto.ProblemResponse;
import com.byteboxcodes.byteboxbackend.dto.TestCaseResponse;
import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Topic;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.TestCaseRepository;
import com.byteboxcodes.byteboxbackend.repository.TopicRespository;
import com.byteboxcodes.byteboxbackend.service.ProblemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRespository problemRespository;
    private final TopicRespository topicRespository;
    private final TestCaseRepository testCaseRepository;

    @Override
    public List<ProblemListResponse> getAllProblems() {
        return problemRespository.findAll().stream()
                .map(problem -> ProblemListResponse.builder()
                        .id(problem.getId())
                        .title(problem.getTitle())
                        .difficulty(problem.getDifficulty().name())
                        .orderIndex(problem.getOrderIndex())
                        .build())
                .toList();

    }

    @Override
    public ProblemResponse getProblemById(UUID id) {

        Problem problem = problemRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        List<TestCaseResponse> sampleTestCases = testCaseRepository
                .findByProblemIdAndIsSampleTrue(id)
                .stream()
                .map(tc -> TestCaseResponse.builder()
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .build())
                .toList();

        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty().name())
                .constraints(problem.getConstraints())
                .requiredKeywords(problem.getRequiredKeywords())
                .topic(new com.byteboxcodes.byteboxbackend.dto.TopicResponse(
                        problem.getTopic().getId(),
                        problem.getTopic().getName(),
                        problem.getTopic().getDescription(),
                        0L))
                .sampleTestCases(sampleTestCases)
                .build();

    }

    @Override
    public List<ProblemListResponse> getProblemsByDifficulty(Difficulty difficulty) {
        return problemRespository.findByDifficulty(difficulty).stream()
                .map(problem -> ProblemListResponse.builder()
                        .id(problem.getId())
                        .title(problem.getTitle())
                        .difficulty(problem.getDifficulty().name())
                        .orderIndex(problem.getOrderIndex())
                        .build())
                .toList();
    }

    @Override
    public List<ProblemListResponse> getProblemsByTopic(String topicName) {
        Topic topic = topicRespository.findByName(topicName).orElseThrow(
                () -> new RuntimeException("Topic not found"));
        return problemRespository.findByTopic(topic).stream()
                .map(problem -> ProblemListResponse.builder()
                        .id(problem.getId())
                        .title(problem.getTitle())
                        .difficulty(problem.getDifficulty().name())
                        .orderIndex(problem.getOrderIndex())
                        .build())
                .toList();
    }
}
