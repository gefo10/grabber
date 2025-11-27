package com.grabbler.config;

import com.stripe.Stripe;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

public class StripeConfig {

    @Value("${stripe.api-key}")
    private String apiKey;


    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
