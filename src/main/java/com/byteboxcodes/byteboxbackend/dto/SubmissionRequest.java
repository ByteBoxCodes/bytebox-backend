package com.byteboxcodes.byteboxbackend.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmissionRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String language;

    @NotBlank
    private UUID userId;

    @NotBlank
    private UUID problemId;

}
