package com.byteboxcodes.byteboxbackend.service;

import java.util.List;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Problem;

public interface ProblemService {

    List<Problem> getAllProblems();

    Problem getProblemById(UUID id);

    List<Problem> getProblemsByDifficulty(Difficulty difficulty);

    List<Problem> getProblemsByTopic(String topic);
}
