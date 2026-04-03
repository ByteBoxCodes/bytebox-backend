package com.byteboxcodes.byteboxbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name; // e.g. "Problem Solver", "Fire Starter"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType type; // TITLE, AVATAR, CARD

    @Column(nullable = false)
    private String value; // e.g. "🔥", "Problem Solver", card theme name

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType; // LEVEL, SOLVED, STREAK

    @Column(name = "condition_value", nullable = false)
    private int conditionValue; // e.g. 5 (level 5), 50 (50 problems), 7 (7-day streak)

    // ── Optional second condition (AND logic) ─────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type2")
    private ConditionType conditionType2; // null = no second condition

    @Column(name = "condition_value2")
    private Integer conditionValue2;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;
}
