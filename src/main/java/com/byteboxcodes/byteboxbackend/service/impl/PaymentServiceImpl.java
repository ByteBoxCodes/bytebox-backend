package com.byteboxcodes.byteboxbackend.service.impl;

import java.time.LocalDateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.byteboxcodes.byteboxbackend.dto.CreateSubscriptionResponse;
import com.byteboxcodes.byteboxbackend.entity.Subscription;
import com.byteboxcodes.byteboxbackend.entity.User;
import com.byteboxcodes.byteboxbackend.repository.SubscriptionRepository;
import com.byteboxcodes.byteboxbackend.repository.UserRepository;
import com.byteboxcodes.byteboxbackend.dto.VerifySubscriptionRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import com.byteboxcodes.byteboxbackend.service.PaymentService;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Value("${razorpay.plan.id:plan_xxxxx}")
    private String planId;

    public CreateSubscriptionResponse createSubscription(String email) throws Exception {

        // Validate user existence
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JSONObject request = new JSONObject();
        request.put("plan_id", planId); 
        request.put("total_count", 12);

        com.razorpay.Subscription razorpaySub = razorpayClient.subscriptions.create(request);

        CreateSubscriptionResponse res = new CreateSubscriptionResponse();
        res.setSubscriptionId(razorpaySub.get("id"));

        return res;
    }

    public void verifyPayment(VerifySubscriptionRequest req, String email) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String payload = req.getRazorpayPaymentId() + "|" + req.getRazorpaySubscriptionId();

        String expectedSignature = generateHmac(payload);

        if (!expectedSignature.equals(req.getRazorpaySignature())) {
            throw new RuntimeException("Invalid payment signature");
        }

        // Save subscription
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setRazorpaySubscriptionId(req.getRazorpaySubscriptionId());
        sub.setStartDate(LocalDateTime.now());
        sub.setExpiryDate(LocalDateTime.now().plusMonths(1));
        sub.setActive(true);
        sub.setStatus("ACTIVE");

        subscriptionRepository.save(sub);

        // Make user premium
        user.setPremium(true);
        userRepository.save(user);
    }

    private String generateHmac(String data) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(razorpaySecret.getBytes(), "HmacSHA256");
        mac.init(key);

        byte[] hash = mac.doFinal(data.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}