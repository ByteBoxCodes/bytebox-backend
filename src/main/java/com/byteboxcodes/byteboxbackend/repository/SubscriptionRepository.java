package com.byteboxcodes.byteboxbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.byteboxcodes.byteboxbackend.entity.Subscription;
import com.byteboxcodes.byteboxbackend.entity.User;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByUser(User user);
}