package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JudgeResult {

    private boolean accepted;
    private String totalTestCases;
    private String passedTestCases;
    private String errorMessage;

}
