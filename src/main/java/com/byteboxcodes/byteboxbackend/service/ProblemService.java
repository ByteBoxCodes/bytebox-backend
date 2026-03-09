package com.byteboxcodes.byteboxbackend.service;

import java.util.List;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.dto.ProblemListResponse;
import com.byteboxcodes.byteboxbackend.dto.ProblemResponse;
import com.byteboxcodes.byteboxbackend.entity.Difficulty;

public interface ProblemService {

    List<ProblemListResponse> getAllProblems();

    ProblemResponse getProblemById(UUID id);

    List<ProblemListResponse> getProblemsByDifficulty(Difficulty difficulty);

    List<ProblemListResponse> getProblemsByTopic(String topic);

    List<ProblemListResponse> getProblemsByTopicId(Long topicId);

    List<ProblemListResponse> searchProblems(String query);
}
