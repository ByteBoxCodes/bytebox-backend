package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Topic;

public interface ProblemRespository extends JpaRepository<Problem, UUID> {
    List<Problem> findByDifficulty(Difficulty difficulty);

    List<Problem> findByTopic(Topic topic);

    long countByIsActiveTrue();

    @Query("""
            SELECT p.topic.id, COUNT(p)
            FROM Problem p
            WHERE p.isActive = true
            GROUP BY p.topic.id
            """)
    List<Object[]> countActiveProblemsGroupedByTopic();

    List<Problem> findByIsActiveTrueOrderByOrderIndexAsc();
}
