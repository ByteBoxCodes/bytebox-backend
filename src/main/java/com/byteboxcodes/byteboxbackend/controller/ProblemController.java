package com.byteboxcodes.byteboxbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.ProblemListResponse;
import com.byteboxcodes.byteboxbackend.dto.ProblemResponse;
import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.service.ProblemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public List<ProblemListResponse> getProblems(@RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String topic) {
        if (difficulty != null) {
            return problemService.getProblemsByDifficulty(difficulty);
        }

        if (topic != null) {
            return problemService.getProblemsByTopic(topic);
        }
        return problemService.getAllProblems();
    }

    @GetMapping("/{id}")
    public ProblemResponse getProblemById(@PathVariable UUID id) {
        return problemService.getProblemById(id);
    }

}
