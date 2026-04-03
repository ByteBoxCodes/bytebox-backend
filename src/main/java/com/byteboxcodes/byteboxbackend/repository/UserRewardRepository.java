package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteboxcodes.byteboxbackend.entity.UserReward;

public interface UserRewardRepository extends JpaRepository<UserReward, UUID> {

    List<UserReward> findByUser_Id(UUID userId);

    boolean existsByUser_IdAndReward_Id(UUID userId, String rewardId);
}
