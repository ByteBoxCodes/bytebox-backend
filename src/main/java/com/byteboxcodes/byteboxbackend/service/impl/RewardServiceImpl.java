package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.RewardResponse;
import com.byteboxcodes.byteboxbackend.entity.Reward;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.entity.UserReward;
import com.byteboxcodes.byteboxbackend.repository.RewardRepository;
import com.byteboxcodes.byteboxbackend.repository.SubmissionRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRewardRepository;
import com.byteboxcodes.byteboxbackend.service.RewardService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: get current authenticated user
    // ─────────────────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Check if user meets the reward condition
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isEligible(User user, Reward reward) {
        // Check first condition (always required)
        if (!checkCondition(user, reward.getConditionType(), reward.getConditionValue())) {
            return false;
        }

        // Check second condition if present (AND logic)
        if (reward.getConditionType2() != null && reward.getConditionValue2() != null) {
            return checkCondition(user, reward.getConditionType2(), reward.getConditionValue2());
        }

        return true;
    }

    private boolean checkCondition(User user, com.byteboxcodes.byteboxbackend.entity.ConditionType type, int target) {
        return switch (type) {
            case LEVEL -> user.getLevel() >= target;
            case SOLVED -> submissionRepository.countSolvedProblems(user.getId()) >= target;
            case STREAK -> user.getMaxStreak() >= target;
            case XP -> (user.getPoints() == null ? 0 : user.getPoints()) >= target;
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Check if a reward is currently equipped by the user
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isEquipped(User user, Reward reward) {
        return switch (reward.getType()) {
            case TITLE -> reward.getValue().equals(user.getActiveTitle());
            case AVATAR -> reward.getValue().equals(user.getActiveAvatar());
            case CARD -> reward.getValue().equals(user.getActiveCard());
            case XP -> false; // XP rewards are not equippable
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/rewards/my — all rewards with eligible/claimed/equipped flags
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public List<RewardResponse> getMyRewards() {
        User user = getCurrentUser();

        List<Reward> allRewards = rewardRepository.findByActiveTrue();

        // If reward table is empty, return empty list gracefully
        if (allRewards.isEmpty()) {
            return List.of();
        }

        // Get claimed reward IDs for this user
        Set<String> claimedRewardIds = userRewardRepository.findByUser_Id(user.getId())
                .stream()
                .map(ur -> ur.getReward().getId())
                .collect(Collectors.toSet());

        return allRewards.stream()
                .map(reward -> RewardResponse.builder()
                        .id(reward.getId())
                        .name(reward.getName())
                        .type(reward.getType())
                        .value(reward.getValue())
                        .conditionType(reward.getConditionType())
                        .conditionValue(reward.getConditionValue())
                        .conditionType2(reward.getConditionType2())
                        .conditionValue2(reward.getConditionValue2())
                        .eligible(isEligible(user, reward))
                        .claimed(claimedRewardIds.contains(reward.getId()))
                        .equipped(isEquipped(user, reward))
                        .build())
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/rewards/claim/{rewardId}
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void claimReward(String rewardId) {
        User user = getCurrentUser();

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (!reward.isActive()) {
            throw new RuntimeException("This reward is no longer available");
        }

        if (userRewardRepository.existsByUser_IdAndReward_Id(user.getId(), rewardId)) {
            throw new RuntimeException("Reward already claimed");
        }

        if (!isEligible(user, reward)) {
            throw new RuntimeException("You don't meet the requirements for this reward");
        }

        UserReward userReward = UserReward.builder()
                .user(user)
                .reward(reward)
                .claimedAt(LocalDateTime.now())
                .build();

        userRewardRepository.save(userReward);

        // If XP type reward, grant bonus XP immediately on claim
        if (reward.getType() == com.byteboxcodes.byteboxbackend.entity.RewardType.XP) {
            int bonusXp = Integer.parseInt(reward.getValue());
            user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + bonusXp);
            user.setLevelXp((user.getLevelXp() == null ? 0 : user.getLevelXp()) + bonusXp);

            // Level up check
            while (true) {
                int requiredXp = 10 + (user.getLevel() * 3);
                if (user.getLevelXp() >= requiredXp) {
                    user.setLevelXp(user.getLevelXp() - requiredXp);
                    user.setLevel(user.getLevel() + 1);
                } else {
                    break;
                }
            }
            userRepository.save(user);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/rewards/equip/{rewardId}
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void equipReward(String rewardId) {
        User user = getCurrentUser();

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (!userRewardRepository.existsByUser_IdAndReward_Id(user.getId(), rewardId)) {
            throw new RuntimeException("You must claim this reward before equipping it");
        }

        switch (reward.getType()) {
            case TITLE -> user.setActiveTitle(reward.getValue());
            case AVATAR -> user.setActiveAvatar(reward.getValue());
            case CARD -> user.setActiveCard(reward.getValue());
            case XP -> throw new RuntimeException("XP rewards cannot be equipped");
        }

        userRepository.save(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/rewards/unequip/{rewardId}
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void unequipReward(String rewardId) {
        User user = getCurrentUser();

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        switch (reward.getType()) {
            case TITLE -> user.setActiveTitle(null);
            case AVATAR -> user.setActiveAvatar(null);
            case CARD -> user.setActiveCard(null);
            case XP -> throw new RuntimeException("XP rewards cannot be unequipped");
        }

        userRepository.save(user);
    }
}
