package com.byteboxcodes.byteboxbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteboxcodes.byteboxbackend.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
