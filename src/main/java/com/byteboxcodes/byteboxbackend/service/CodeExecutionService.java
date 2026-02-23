package com.byteboxcodes.byteboxbackend.service;

public interface CodeExecutionService {

    String execute(String code, String language, String input);

}