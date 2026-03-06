package com.byteboxcodes.byteboxbackend.service;

import java.util.UUID;

import com.byteboxcodes.byteboxbackend.dto.JudgeResult;

public interface JudgeService {
    JudgeResult judge(UUID problemId, String code, String language);

    JudgeResult judgeSample(UUID problemId, String code, String language);
}
