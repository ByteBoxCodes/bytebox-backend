package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteboxcodes.byteboxbackend.entity.ConditionType;
import com.byteboxcodes.byteboxbackend.entity.Reward;
import com.byteboxcodes.byteboxbackend.entity.RewardType;

public interface RewardRepository extends JpaRepository<Reward, String> {

    List<Reward> findByActiveTrue();

    List<Reward> findByTypeAndActiveTrue(RewardType type);

    List<Reward> findByConditionTypeAndConditionValueLessThanEqualAndActiveTrue(
            ConditionType conditionType, int conditionValue);
}
