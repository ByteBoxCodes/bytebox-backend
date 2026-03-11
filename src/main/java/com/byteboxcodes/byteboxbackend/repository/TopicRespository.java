package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteboxcodes.byteboxbackend.entity.Topic;

public interface TopicRespository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByName(String name);
    List<Topic> findAllByIsActiveTrue();
}
