package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.CreateSubscriptionResponse;
import com.byteboxcodes.byteboxbackend.dto.VerifySubscriptionRequest;

public interface PaymentService {
    CreateSubscriptionResponse createSubscription(String email) throws Exception;

    void verifyPayment(VerifySubscriptionRequest request, String email) throws Exception;
}
