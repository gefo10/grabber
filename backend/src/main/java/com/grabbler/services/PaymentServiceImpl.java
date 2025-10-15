package com.grabbler.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grabbler.enums.PaymentStatus;
import com.grabbler.models.Order;
import com.grabbler.models.Payment;
import com.grabbler.payloads.payment.*;
import com.grabbler.repositories.PaymentRepository;
import com.grabbler.repositories.OrderRepository;
import jakarta.transaction.Transactional;

import com.grabbler.enums.OrderStatus;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Payment processPayment(PaymentDTO paymentDetails) {

        Payment payment = new Payment();
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setPaymentToken(paymentDetails.getPaymentToken());

        payment.setPaymentStatus(PaymentStatus.PENDING);
        // --- Important Note on Security ---
        // PaymentDTO should NOT contain credit card numbers or CVVs.
        // It should only contain a secure token from a payment gateway.

        // TODO:
        // call a third-party payment gateway's API
        // here to process the transaction. The gateway's response would determine
        // the final status of the payment.

        // For example:
        // boolean transactionSuccess =
        // stripeService.process(payment.getPaymentToken());
        // if (transactionSuccess) {
        // payment.setStatus(PaymentStatus.COMPLETED);
        // } else {
        // payment.setStatus(PaymentStatus.FAILED);
        // }
        // -------------------------------------

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentDetails(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

    }

    @Override
    @Transactional
    public boolean refundPayment(Long paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);

        if (payment.isPresent()) {
            Payment existingPayment = payment.get();
            if (existingPayment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                throw new RuntimeException("Only completed payments can be refunded.");
            }

            // Call the external payment gateway's API to process the refund.
            // This is a conceptual example; the actual implementation depends on your
            // provider's SDK.
            // try {
            // stripeService.refund(payment.getPaymentToken());
            // } catch (StripeException e) {
            // payment.setStatus(PaymentStatus.FAILED_REFUND);
            // paymentRepository.save(payment);
            // throw new APIException("Refund failed for payment ID " + paymentId + ": " +
            // e.getMessage());
            // }
            //

            existingPayment.setPaymentStatus(PaymentStatus.REFUNDED);
            Payment refundedPayment = paymentRepository.save(existingPayment);

            Long orderId = refundedPayment.getOrder().getOrderId();
            orderRepository.findById(orderId).ifPresent(order -> {
                order.setOrderStatus(OrderStatus.REFUNDED);
                orderRepository.save(order);
            });

            return true;

        } else {
            throw new RuntimeException("Payment not found with id: " + paymentId);
        }

    }

    @Override
    public void updatePaymentStatus(Long paymentId, String newStatus) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setPaymentStatus(PaymentStatus.valueOf(newStatus));
            paymentRepository.save(payment);
        } else {
            throw new RuntimeException("Payment not found with id: " + paymentId);
        }
    }
}
