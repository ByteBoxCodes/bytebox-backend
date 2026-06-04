package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.Judge0Request;
import com.byteboxcodes.byteboxbackend.dto.Judge0Response;
import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;
import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.dto.TestCaseResult;
import com.byteboxcodes.byteboxbackend.entity.TestCase;
import com.byteboxcodes.byteboxbackend.repository.TestCaseRepository;
import com.byteboxcodes.byteboxbackend.service.DockerExecutionService;
import com.byteboxcodes.byteboxbackend.service.JudgeService;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final TestCaseRepository testCaseRepository;
    private final DockerExecutionService dockerExecutionService;

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${judge0.api.key}")
    private String rapidApiKey;
    private static final String JUDGE0_URL = "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=true&wait=true";

    @Override
    public JudgeResult judge(UUID problemId, String code, String language) {
        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);
        
        // if (List.of("PYTHON", "JAVA", "C", "C++").contains(language.toUpperCase())) {
        //     return judgeWithDocker(testCases, code, language);
        // }
        
        return judgeWithRapidApi(testCases, code, language);
    }

    @Override
    public JudgeResult judgeSample(UUID problemId, String code, String language) {
        List<TestCase> testCases = testCaseRepository.findByProblemIdAndIsSampleTrue(problemId);
        
        // if (List.of("PYTHON", "JAVA", "C", "C++").contains(language.toUpperCase())) {
        //     return judgeWithDocker(testCases, code, language);
        // }
        
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

    private String encode(String val) {
        if (val == null)
            return null;
        return Base64.getEncoder().encodeToString(val.getBytes());
    }

    private String decode(String val) {
        if (val == null)
            return null;
        return new String(Base64.getDecoder().decode(val));
    }

    private Integer getLanguageId(String language) {
        return switch (language.toUpperCase()) {
            case "JAVA" -> 62;
            case "PYTHON" -> 71;
            case "C++", "CPP" -> 54;
            case "C" -> 50;
            default -> throw new RuntimeException("Unsupported language");
        };
    }

    private JudgeResult judgeWithRapidApi(List<TestCase> testCases, String code, String language) {
        int total = testCases.size();
        int passed = 0;

        List<TestCaseResult> results = new ArrayList<>(total);
        boolean hasFailure = false;

        for (TestCase testCase : testCases) {

            Judge0Request requestBody = Judge0Request.builder()
                    .language_id(getLanguageId(language))
                    .source_code(encode(code))
                    .stdin(encode(testCase.getInput()))
                    .expected_output(encode(testCase.getExpectedOutput()))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", rapidApiKey);
            headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Judge0Request> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Judge0Response> response;

            try {
                response = restTemplate.postForEntity(JUDGE0_URL, entity, Judge0Response.class);
            } catch (HttpClientErrorException e) {
                return JudgeResult.runtimeError(
                        "Judge0 API Error: " + e.getResponseBodyAsString(), total, passed);
            }

            Judge0Response body = response.getBody();

            if (body == null) {
                return JudgeResult.runtimeError("No response from Judge0", total, passed);
            }

            if (body.getCompile_output() != null && !body.getCompile_output().trim().isEmpty()) {
                return JudgeResult.compileError(
                        decode(body.getCompile_output()), total, passed);
            }

            if (body.getStderr() != null && !body.getStderr().trim().isEmpty()) {
                return JudgeResult.runtimeError(
                        decode(body.getStderr()), total, passed);
            }

            String userOutput = decode(body.getStdout());
            String expectedOutput = testCase.getExpectedOutput();
            
            boolean accepted = false;
            if (userOutput != null && expectedOutput != null) {
                accepted = userOutput.trim().equalsIgnoreCase(expectedOutput.trim());
            }

            if (!"Accepted".equalsIgnoreCase(body.getStatus().getDescription()) || !accepted) {
                hasFailure = true;
            } else {
                passed++;
            }

            results.add(TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(expectedOutput)
                    .userOutput(userOutput != null ? userOutput.trim() : null)
                    .status(hasFailure && !accepted ? "WRONG_ANSWER" : "PASSED")
                    .build());
        }

        return hasFailure
                ? JudgeResult.wrongAnswer(total, passed, results)
                : JudgeResult.success(total, passed, results);
    }
}