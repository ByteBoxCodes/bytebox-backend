package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;
import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.dto.TestCaseResult;
import com.byteboxcodes.byteboxbackend.entity.TestCase;
import com.byteboxcodes.byteboxbackend.repository.TestCaseRepository;
import com.byteboxcodes.byteboxbackend.service.DockerExecutionService;
import com.byteboxcodes.byteboxbackend.service.JudgeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final TestCaseRepository testCaseRepository;
    private final DockerExecutionService dockerExecutionService;

    // RapidAPI/Judge0 Engine is fully retired and replaced natively by Docker Engine
    // private final RestTemplate restTemplate = new RestTemplate();
    // @Value("${JUDGE0_API_KEY}")
    // private String rapidApiKey;
    // private static final String JUDGE0_URL = "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=true&wait=true";

    @Override
    public JudgeResult judge(UUID problemId, String code, String language) {
        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);
        
        if (List.of("PYTHON", "JAVA", "C", "C++").contains(language.toUpperCase())) {
            return judgeWithDocker(testCases, code, language);
        }
        
        return judgeWithRapidApi(testCases, code, language);
    }

    @Override
    public JudgeResult judgeSample(UUID problemId, String code, String language) {
        List<TestCase> testCases = testCaseRepository.findByProblemIdAndIsSampleTrue(problemId);
        
        if (List.of("PYTHON", "JAVA", "C", "C++").contains(language.toUpperCase())) {
            return judgeWithDocker(testCases, code, language);
        }
        
        return judgeWithRapidApi(testCases, code, language);
    }

    private JudgeResult judgeWithDocker(List<TestCase> testCases, String code, String language) {
        int total = testCases.size();
        int passed = 0;

        List<TestCaseResult> results = new ArrayList<>(total);
        boolean hasFailure = false;

        for (TestCase testCase : testCases) {
            DockerExecutionResult dockerResult = dockerExecutionService.runCode(language, code, testCase.getInput());

            if (dockerResult.isTimeout()) {
                return JudgeResult.runtimeError("Execution Timed Out", total, passed);
            }

            if (dockerResult.getExitCode() != 0) {
                // Determine if it was a syntax error/runtime error based on stderr
                String errorMsg = dockerResult.getStderr() != null && !dockerResult.getStderr().isEmpty()
                        ? dockerResult.getStderr()
                        : "Unknown Error (Exit status: " + dockerResult.getExitCode() + ")";
                return JudgeResult.runtimeError(errorMsg, total, passed);
            }

            String userOutput = dockerResult.getStdout();
            String expectedOutput = testCase.getExpectedOutput();
            
            boolean accepted = false;
            if (userOutput != null && expectedOutput != null) {
                accepted = userOutput.trim().equalsIgnoreCase(expectedOutput.trim());
            }

            if (accepted) {
                passed++;
            } else {
                hasFailure = true;
            }

            results.add(TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(expectedOutput)
                    .userOutput(userOutput != null ? userOutput.trim() : null)
                    .status(accepted ? "PASSED" : "WRONG_ANSWER")
                    .build());
        }

        return hasFailure
                ? JudgeResult.wrongAnswer(total, passed, results)
                : JudgeResult.success(total, passed, results);
    }

    private JudgeResult judgeWithRapidApi(List<TestCase> testCases, String code, String language) {
        throw new RuntimeException("RapidAPI Engine is fully retired. Local Docker Execution is handling requests.");
    }
}