package com.byteboxcodes.byteboxbackend.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String language;

    @NotNull
    private UUID problemId;

}
