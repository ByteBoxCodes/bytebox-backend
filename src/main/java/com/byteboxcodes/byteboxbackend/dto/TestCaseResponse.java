package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestCaseResponse {
    private String input;
    private String expectedOutput;
    private Boolean isSample;
}
