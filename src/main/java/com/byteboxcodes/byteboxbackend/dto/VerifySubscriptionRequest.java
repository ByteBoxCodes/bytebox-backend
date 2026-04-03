package com.byteboxcodes.byteboxbackend.dto;

import lombok.Data;

@Data
public class VerifySubscriptionRequest {
    private String razorpaySubscriptionId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}