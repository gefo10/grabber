package com.grabbler.payloads.payment;

import com.grabbler.enums.PaymentMethod;
import com.grabbler.enums.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    @NotNull(message = "Payment token is required")
    private String paymentToken;

}
