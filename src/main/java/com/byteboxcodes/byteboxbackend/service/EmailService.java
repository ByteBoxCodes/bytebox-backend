package com.byteboxcodes.byteboxbackend.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
}
