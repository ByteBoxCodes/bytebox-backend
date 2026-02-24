package com.byteboxcodes.byteboxbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.TopicResponse;
import com.byteboxcodes.byteboxbackend.service.TopicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public List<TopicResponse> getAllTopics() {
        return topicService.getAllTopicsWithProblemCount();
    }
}
