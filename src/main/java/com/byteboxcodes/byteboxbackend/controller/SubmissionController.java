package com.byteboxcodes.byteboxbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.SubmissionRequest;
import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.service.SubmissionService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public Submission submit(@RequestBody SubmissionRequest request) {
        return submissionService.submitSolution(request);
    }

    @GetMapping("/user/{userId}")
    public List<Submission> getSubmissionsByUserId(@PathVariable UUID userId) {
        return submissionService.getSubmissionsByUser(userId);
    }

    @GetMapping("/problem/{problemId}")
    public List<Submission> getSubmissionsByProblemId(@PathVariable UUID problemId) {
        return submissionService.getSubmissionsByProblem(problemId);
    }

}
