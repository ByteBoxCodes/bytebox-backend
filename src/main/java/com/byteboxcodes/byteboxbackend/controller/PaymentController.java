package com.byteboxcodes.byteboxbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.byteboxcodes.byteboxbackend.dto.CreateSubscriptionResponse;
import com.byteboxcodes.byteboxbackend.dto.VerifySubscriptionRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import com.byteboxcodes.byteboxbackend.service.PaymentService;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-subscription")
    public CreateSubscriptionResponse create() throws Exception {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return paymentService.createSubscription(email);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifySubscriptionRequest req) throws Exception {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        paymentService.verifyPayment(req, email);

        return ResponseEntity.ok("Payment verified");
    }
}