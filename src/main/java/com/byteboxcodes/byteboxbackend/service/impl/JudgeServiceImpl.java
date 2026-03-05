package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.byteboxcodes.byteboxbackend.dto.Judge0Request;
import com.byteboxcodes.byteboxbackend.dto.Judge0Response;
import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.dto.TestCaseResult;
import com.byteboxcodes.byteboxbackend.entity.TestCase;
import com.byteboxcodes.byteboxbackend.repository.TestCaseRepository;
import com.byteboxcodes.byteboxbackend.service.JudgeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final TestCaseRepository testCaseRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${JUDGE0_API_KEY}")
    private String rapidApiKey;

    private static final String JUDGE0_URL = "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=true&wait=true";

    private String encode(String val) {
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
            case "C++" -> 54;
            default -> throw new RuntimeException("Unsupported language");
        };
    }

    @Override
    public JudgeResult judge(UUID problemId, String code, String language) {

        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);
        int total = testCases.size();
        int passed = 0;

        List<TestCaseResult> results = new ArrayList<>(total);
        boolean hasFailure = false;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", rapidApiKey);
        headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String encodedCode = encode(code);
        int languageId = getLanguageId(language);

        for (TestCase testCase : testCases) {

            Judge0Request requestBody = Judge0Request.builder()
                    .language_id(languageId)
                    .source_code(encodedCode)
                    .stdin(encode(testCase.getInput()))
                    .expected_output(encode(testCase.getExpectedOutput()))
                    .build();

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

            // Compile error — return immediately (no point running more tests)
            if (body.getCompile_output() != null) {
                return JudgeResult.compileError(decode(body.getCompile_output()), total, passed);
            }

            // Runtime error — return immediately
            if (body.getStderr() != null) {
                return JudgeResult.runtimeError(decode(body.getStderr()), total, passed);
            }

            // Build per-test-case result
            String userOutput = decode(body.getStdout());
            String expectedOutput = testCase.getExpectedOutput();
            boolean accepted = "Accepted".equalsIgnoreCase(
                    body.getStatus().getDescription());

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
}