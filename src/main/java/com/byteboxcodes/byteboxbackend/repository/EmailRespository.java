package com.byteboxcodes.byteboxbackend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteboxcodes.byteboxbackend.entity.EmailVerification;

public interface EmailRespository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findByToken(String token);
}
