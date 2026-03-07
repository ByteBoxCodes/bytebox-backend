package com.byteboxcodes.byteboxbackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String email, String token) {

        String verificationLink = frontendUrl + "/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify Your Email - ByteBoxCodes");
        message.setText(
                "Click the link below to verify your email:\n\n"
                        + verificationLink +
                        "\n\nThis link expires in 24 hours.");

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Your Password - ByteBoxCodes");
        message.setText(
                "Click the link below to reset your password:\n\n"
                        + resetLink +
                        "\n\nThis link expires in 1 hour if not used.");

        mailSender.send(message);
    }
}