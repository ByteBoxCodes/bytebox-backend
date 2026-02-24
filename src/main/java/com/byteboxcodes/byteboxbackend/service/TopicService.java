package com.byteboxcodes.byteboxbackend.service;

import java.util.List;

import com.byteboxcodes.byteboxbackend.dto.TopicResponse;
import com.byteboxcodes.byteboxbackend.entity.Topic;

public interface TopicService {

    List<Topic> getAllTopics();

    List<TopicResponse> getAllTopicsWithProblemCount();

}
