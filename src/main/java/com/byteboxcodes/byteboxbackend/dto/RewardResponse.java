package com.byteboxcodes.byteboxbackend.dto;

import com.byteboxcodes.byteboxbackend.entity.ConditionType;
import com.byteboxcodes.byteboxbackend.entity.RewardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RewardResponse {

    private String id;
    private String name;
    private RewardType type;     // TITLE, AVATAR, CARD
    private String value;

    private ConditionType conditionType; // LEVEL, SOLVED, STREAK
    private int conditionValue;

    private ConditionType conditionType2; // null if single condition
    private Integer conditionValue2;

    private boolean eligible;   // user meets the condition
    private boolean claimed;    // user already claimed it
    private boolean equipped;   // currently active on profile
}
