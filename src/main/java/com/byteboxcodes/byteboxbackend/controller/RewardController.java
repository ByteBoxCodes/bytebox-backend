package com.byteboxcodes.byteboxbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteboxcodes.byteboxbackend.dto.ApiResponse;
import com.byteboxcodes.byteboxbackend.dto.RewardResponse;
import com.byteboxcodes.byteboxbackend.service.RewardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/my")
    public ApiResponse<List<RewardResponse>> getMyRewards() {
        List<RewardResponse> rewards = rewardService.getMyRewards();
        return ApiResponse.<List<RewardResponse>>builder()
                .success(true)
                .message("Rewards fetched successfully")
                .data(rewards)
                .build();
    }

    @PostMapping("/claim/{rewardId}")
    public ApiResponse<String> claimReward(@PathVariable String rewardId) {
        rewardService.claimReward(rewardId);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Reward claimed successfully")
                .data("Reward claimed")
                .build();
    }

    @PutMapping("/equip/{rewardId}")
    public ApiResponse<String> equipReward(@PathVariable String rewardId) {
        rewardService.equipReward(rewardId);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Reward equipped successfully")
                .data("Reward equipped")
                .build();
    }

    @PutMapping("/unequip/{rewardId}")
    public ApiResponse<String> unequipReward(@PathVariable String rewardId) {
        rewardService.unequipReward(rewardId);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Reward unequipped successfully")
                .data("Reward unequipped")
                .build();
    }
}
