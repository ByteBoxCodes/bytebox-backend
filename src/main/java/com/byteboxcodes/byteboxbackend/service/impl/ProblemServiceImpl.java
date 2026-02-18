package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.service.ProblemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRespository problemRespository;

    @Override
    public List<Problem> getAllProblems() {
        return problemRespository.findAll();
    }

    @Override
    public Problem getProblemById(UUID id) {
        return problemRespository.findById(id).orElseThrow(
                () -> new RuntimeException("Problem not found"));
    }

    @Override
    public List<Problem> getProblemsByDifficulty(Difficulty difficulty) {
        return problemRespository.findByDifficulty(difficulty);
    }

    @Override
    public List<Problem> getProblemsByTopic(String topic) {
        return problemRespository.findByTopic(topic);
    }
}
