package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byteboxcodes.byteboxbackend.dto.LeaderboardResponse;
import com.byteboxcodes.byteboxbackend.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("""
                SELECT new com.byteboxcodes.byteboxbackend.dto.LeaderboardResponse(
                    u.username, u.name, u.avatarUrl, u.points, u.level,
                    COALESCE((SELECT COUNT(DISTINCT s.problem.id) FROM com.byteboxcodes.byteboxbackend.entity.Submission s WHERE s.user.id = u.id AND s.status = 'ACCEPTED'), 0)
                )
                FROM User u
                ORDER BY u.points DESC
            """)
    List<LeaderboardResponse> getLeaderboard();
}
