package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.ProblemListResponse;
import com.byteboxcodes.byteboxbackend.dto.ProblemResponse;
import com.byteboxcodes.byteboxbackend.dto.TestCaseResponse;
import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.byteboxcodes.byteboxbackend.entity.Topic;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.TestCaseRepository;
import com.byteboxcodes.byteboxbackend.repository.TopicRespository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.service.ProblemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
        private final ProblemRespository problemRespository;
        private final TopicRespository topicRespository;
        private final TestCaseRepository testCaseRepository;
        private final UserRepository userRepository;
        private final SubmissionRepository submissionRepository;

        private Set<UUID> getSolvedProblemIds() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                Set<UUID> solvedSet = new HashSet<>();

                // ✅ Only if user is actually logged in
                if (authentication != null
                                && authentication.isAuthenticated()
                                && !(authentication instanceof AnonymousAuthenticationToken)) {

                        String email = authentication.getName();

                        User user = userRepository.findByEmail(email)
                                        .orElse(null);

                        if (user != null) {
                                List<UUID> solvedIds = submissionRepository.findSolvedProblemIdsByUser(user.getId(),
                                                SubmissionStatus.ACCEPTED);

                                solvedSet.addAll(solvedIds);
                        }
                }

                return solvedSet;
        }

        @Override
        public List<ProblemListResponse> getAllProblems() {

                Set<UUID> solvedSet = getSolvedProblemIds();

                List<Problem> problems = problemRespository.findByIsActiveTrueOrderByOrderIndexAsc();

                return problems.stream()
                                .map(problem -> ProblemListResponse.builder()
                                                .id(problem.getId())
                                                .title(problem.getTitle())
                                                .difficulty(problem.getDifficulty().name())
                                                .orderIndex(problem.getOrderIndex())
                                                .isSolved(solvedSet.contains(problem.getId()))
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
                                                .explanation(tc.getExplanation())
                                                .isSample(tc.getIsSample())
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
                                .instructions(problem.getInstructions())
                                .orderIndex(problem.getOrderIndex())
                                .isActive(problem.getIsActive())
                                .build();

        }

        @Override
        public List<ProblemListResponse> getProblemsByDifficulty(Difficulty difficulty) {
                Set<UUID> solvedSet = getSolvedProblemIds();

                return problemRespository.findByDifficultyAndIsActiveTrueOrderByOrderIndexAsc(difficulty).stream()
                                .map(problem -> ProblemListResponse.builder()
                                                .id(problem.getId())
                                                .title(problem.getTitle())
                                                .difficulty(problem.getDifficulty().name())
                                                .orderIndex(problem.getOrderIndex())
                                                .isSolved(solvedSet.contains(problem.getId()))
                                                .build())
                                .toList();
        }

        @Override
        public List<ProblemListResponse> getProblemsByTopic(String topicName) {
                Set<UUID> solvedSet = getSolvedProblemIds();

                Topic topic = topicRespository.findByName(topicName).orElseThrow(
                                () -> new RuntimeException("Topic not found"));
                return problemRespository.findByTopicAndIsActiveTrueOrderByOrderIndexAsc(topic).stream()
                                .map(problem -> ProblemListResponse.builder()
                                                .id(problem.getId())
                                                .title(problem.getTitle())
                                                .difficulty(problem.getDifficulty().name())
                                                .orderIndex(problem.getOrderIndex())
                                                .isSolved(solvedSet.contains(problem.getId()))
                                                .build())
                                .toList();
        }

        @Override
        public List<ProblemListResponse> getProblemsByTopicId(Long topicId) {
                Set<UUID> solvedSet = getSolvedProblemIds();

                return problemRespository.findByTopicIdAndIsActiveTrueOrderByOrderIndexAsc(topicId).stream()
                                .map(problem -> ProblemListResponse.builder()
                                                .id(problem.getId())
                                                .title(problem.getTitle())
                                                .difficulty(problem.getDifficulty().name())
                                                .orderIndex(problem.getOrderIndex())
                                                .isSolved(solvedSet.contains(problem.getId()))
                                                .build())
                                .toList();
        }

        @Override
        public List<ProblemListResponse> searchProblems(String query) {
                Set<UUID> solvedSet = getSolvedProblemIds();

                return problemRespository.searchProblems(query).stream()
                                .map(problem -> ProblemListResponse.builder()
                                                .id(problem.getId())
                                                .title(problem.getTitle())
                                                .difficulty(problem.getDifficulty().name())
                                                .orderIndex(problem.getOrderIndex())
                                                .isSolved(solvedSet.contains(problem.getId()))
                                                .build())
                                .toList();
        }
}
