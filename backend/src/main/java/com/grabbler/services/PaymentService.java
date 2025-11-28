package com.grabbler.services;

import com.grabbler.models.Payment;
import com.grabbler.payloads.payment.*;

public interface PaymentService {
  Payment processPayment(PaymentDTO paymentDetails);

  Payment getPaymentDetails(Long paymentId);

  boolean refundPayment(Long paymentId);

  void updatePaymentStatus(Long paymentId, String newStatus);
}
