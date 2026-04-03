package com.byteboxcodes.byteboxbackend.service;

import java.util.List;

import com.byteboxcodes.byteboxbackend.dto.RewardResponse;

public interface RewardService {

    List<RewardResponse> getMyRewards();

    void claimReward(String rewardId);

    void equipReward(String rewardId);

    void unequipReward(String rewardId);
}
