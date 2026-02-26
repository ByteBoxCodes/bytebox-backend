package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.entity.SubmissionStatus;
import com.byteboxcodes.byteboxbackend.entity.User;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
        List<Submission> findByUser(User user);

        List<Submission> findByProblem(Problem problem);

        List<Submission> findByUserIdAndProblemId(UUID userId, UUID problemId);

        @Query("""
                        SELECT s.problem.id
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.status = :status
                        """)
        List<UUID> findSolvedProblemIdsByUser(UUID userId, SubmissionStatus status);

        @Query("""
                        SELECT s.problem.topic.id, COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.status = :status
                        GROUP BY s.problem.topic.id
                        """)
        List<Object[]> countSolvedProblemsGroupedByTopic(
                        UUID userId,
                        SubmissionStatus status);
}
