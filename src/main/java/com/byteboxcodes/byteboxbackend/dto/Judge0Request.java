package com.byteboxcodes.byteboxbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Judge0Request {
    private Integer language_id;
    private String source_code;
    private String stdin;
    private String expected_output;
}