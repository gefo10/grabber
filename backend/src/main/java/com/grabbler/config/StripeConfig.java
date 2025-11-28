package com.grabbler.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

public class StripeConfig {

  @Value("${stripe.api-key}")
  private String apiKey;

  @PostConstruct
  public void init() {
    Stripe.apiKey = apiKey;
  }
}
