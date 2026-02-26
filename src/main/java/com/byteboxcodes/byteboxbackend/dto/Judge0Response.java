package com.byteboxcodes.byteboxbackend.dto;

import lombok.Data;

@Data
public class Judge0Response {
    private String stdout;
    private String stderr;
    private String compile_output;
    private Status status;

    @Data
    public class Status {
        private Integer id;
        private String description;
    }
}
