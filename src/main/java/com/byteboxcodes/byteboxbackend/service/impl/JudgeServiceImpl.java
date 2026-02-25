package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.entity.TestCase;
import com.byteboxcodes.byteboxbackend.repository.TestCaseRepository;
import com.byteboxcodes.byteboxbackend.service.CodeExecutionService;
import com.byteboxcodes.byteboxbackend.service.JudgeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final TestCaseRepository testCaseRepository;
    private final CodeExecutionService codeExecutionService;

    @Override
    public JudgeResult judgeSample(UUID problemId, String code, String language) {
        List<TestCase> testCases = testCaseRepository.findByProblemIdAndIsSampleTrue(problemId);
        int passed = 0;
        for (TestCase testCase : testCases) {
            String output = codeExecutionService.execute(code, language, testCase.getInput());

            if (!output.trim().equals(testCase.getExpectedOutput().trim())) {
                return JudgeResult.builder()
                        .accepted(false)
                        .totalTestCases(String.valueOf(testCases.size()))
                        .passedTestCases(String.valueOf(passed))
                        .errorMessage("Wrong Answer on test case " + (passed + 1))
                        .build();
            }

            passed++;

        }

        return JudgeResult.builder()
                .accepted(true)
                .totalTestCases(String.valueOf(testCases.size()))
                .passedTestCases(String.valueOf(passed))
                .build();
    }

    @Override
    public JudgeResult judge(UUID problemId, String code, String language) {
        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);

        int passed = 0;

        for (TestCase testCase : testCases) {
            String output = codeExecutionService.execute(code, language, testCase.getInput());

            if (!output.trim().equals(testCase.getExpectedOutput().trim())) {
                return JudgeResult.builder()
                        .accepted(false)
                        .totalTestCases(String.valueOf(testCases.size()))
                        .passedTestCases(String.valueOf(passed))
                        .errorMessage("Wrong Answer")
                        .build();
            }

            passed++;

        }

        return JudgeResult.builder()
                .accepted(true)
                .totalTestCases(String.valueOf(testCases.size()))
                .passedTestCases(String.valueOf(passed))
                .build();

    }

}
