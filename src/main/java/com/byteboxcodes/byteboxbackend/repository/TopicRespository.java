package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byteboxcodes.byteboxbackend.dto.TopicResponse;
import com.byteboxcodes.byteboxbackend.entity.Topic;

public interface TopicRespository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByName(String name);

    @Query("""
            SELECT new com.byteboxcodes.byteboxbackend.dto.TopicResponse(
                 t.id,
                 t.name,
                 t.description,
                 COUNT(p.id)
            )
            FROM Topic t
            LEFT JOIN Problem p ON p.topic.id = t.id
            GROUP BY t.id, t.name, t.description
            """)
    List<TopicResponse> findAllTopicsWithProblemCount();

}
