package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestCaseResult {
    private String input;
    private String expectedOutput;
    private String userOutput;
    private String status;
}