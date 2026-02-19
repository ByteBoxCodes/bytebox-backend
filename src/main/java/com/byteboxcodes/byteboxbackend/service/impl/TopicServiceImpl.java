package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.entity.Topic;
import com.byteboxcodes.byteboxbackend.repository.TopicRespository;
import com.byteboxcodes.byteboxbackend.service.TopicService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRespository topicRespository;

    @Override
    public List<Topic> getAllTopics() {
        return topicRespository.findAll();
    }

}
