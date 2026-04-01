package com.byteboxcodes.byteboxbackend.service.impl;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.PlatformResponse;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.TopicRespository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.service.PlatformService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {

    private final UserRepository userRepository;
    private final ProblemRespository problemRespository;
    private final TopicRespository topicRespository;

    @Override
    public PlatformResponse getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalProblems = problemRespository.countByIsActiveTrue();
        long totalTopics = topicRespository.countByIsActiveTrue();

        return PlatformResponse.builder()
                .totalUsers(totalUsers)
                .totalProblems(totalProblems)
                .totalTopics(totalTopics)
                .build();
    }
}
