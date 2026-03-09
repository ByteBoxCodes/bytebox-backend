package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byteboxcodes.byteboxbackend.entity.Difficulty;
import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Topic;

public interface ProblemRespository extends JpaRepository<Problem, UUID> {
        List<Problem> findByDifficultyAndIsActiveTrueOrderByOrderIndexAsc(Difficulty difficulty);

        List<Problem> findByTopicAndIsActiveTrueOrderByOrderIndexAsc(Topic topic);

        List<Problem> findByTopicIdAndIsActiveTrueOrderByOrderIndexAsc(Long topicId);

        long countByIsActiveTrue();

        long countByDifficultyAndIsActiveTrue(Difficulty difficulty);

        @Query("""
                        SELECT p.difficulty, COUNT(p)
                        FROM Problem p
                        WHERE p.isActive = true
                        GROUP BY p.difficulty
                        """)
        List<Object[]> countActiveProblemsGroupedByDifficulty();

        @Query("""
                        SELECT p.topic.id, COUNT(p)
                        FROM Problem p
                        WHERE p.isActive = true
                        GROUP BY p.topic.id
                        """)
        List<Object[]> countActiveProblemsGroupedByTopic();

        List<Problem> findByIsActiveTrueOrderByOrderIndexAsc();

        @Query("""
                        SELECT p
                        FROM Problem p
                        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
                        AND p.isActive = true
                        ORDER BY p.orderIndex ASC
                        """)
        List<Problem> searchProblems(String query);
}
