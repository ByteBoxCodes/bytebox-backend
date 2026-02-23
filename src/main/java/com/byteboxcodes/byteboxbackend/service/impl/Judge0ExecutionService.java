package com.byteboxcodes.byteboxbackend.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.byteboxcodes.byteboxbackend.service.CodeExecutionService;

@Service
public class Judge0ExecutionService implements CodeExecutionService {

    @Value("${judge0.api.url}")
    private String apiUrl;

    @Value("${judge0.api.key}")
    private String apiKey;

    @Value("${judge0.api.host}")
    private String apiHost;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String execute(String code, String language, String input) {

        int languageId = getLanguageId(language);

        Map<String, Object> body = new HashMap<>();
        body.put("source_code", code);
        body.put("language_id", languageId);
        body.put("stdin", input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

        Map result = response.getBody();

        if (result == null) {
            throw new RuntimeException("Judge0 returned null response");
        }

        if (result.get("stdout") != null) {
            return result.get("stdout").toString().trim();
        }

        if (result.get("compile_output") != null) {
            throw new RuntimeException("Compilation Error");
        }

        if (result.get("stderr") != null) {
            throw new RuntimeException("Runtime Error");
        }

        return "";
    }

    private int getLanguageId(String language) {
        return switch (language.toUpperCase()) {
            case "JAVA" -> 62;
            case "PYTHON" -> 71;
            case "C++" -> 54;
            default -> throw new RuntimeException("Unsupported language");
        };
    }
}