package com.grabbler.payloads.order;

import com.grabbler.payloads.payment.PaymentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
  @Valid
  @NotNull(message = "Payment details are required")
  private PaymentDTO payment;
}
