package com.byteboxcodes.byteboxbackend.dto;

import com.byteboxcodes.byteboxbackend.entity.ProgrammingLanguage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreferredLanguageRequest {
    private ProgrammingLanguage preferredLanguage;
}
