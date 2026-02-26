package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.TopicResponse;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.byteboxcodes.byteboxbackend.entity.Topic;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.repository.ProblemRespository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.TopicRespository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.service.TopicService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRespository topicRespository;
    private final ProblemRespository problemRespository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Override
    public List<TopicResponse> getAllTopics() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = null;

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            String email = authentication.getName();
            user = userRepository.findByEmail(email).orElse(null);
        }

        List<Topic> topics = topicRespository.findAll();

        Map<Long, Long> totalMap = new HashMap<>();

        problemRespository.countActiveProblemsGroupedByTopic()
                .forEach(obj -> {
                    Long topicId = (Long) obj[0];
                    Long count = (Long) obj[1];
                    totalMap.put(topicId, count);
                });

        Map<Long, Long> solvedMap = new HashMap<>();

        if (user != null) {
            submissionRepository.countSolvedProblemsGroupedByTopic(
                    user.getId(),
                    SubmissionStatus.ACCEPTED)
                    .forEach(obj -> {
                        Long topicId = (Long) obj[0];
                        Long count = (Long) obj[1];
                        solvedMap.put(topicId, count);
                    });
        }

        return topics.stream()
                .map(topic -> {

                    Long total = totalMap.getOrDefault(topic.getId(), 0L);
                    Long solved = solvedMap.getOrDefault(topic.getId(), 0L);

                    return TopicResponse.builder()
                            .id(topic.getId())
                            .name(topic.getName())
                            .description(topic.getDescription())
                            .totalProblems(total)
                            .solvedProblems(solved)
                            .build();
                })
                .toList();
    }

}
