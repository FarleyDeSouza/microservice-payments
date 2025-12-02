package com.clickbait.payments.domain.ports.in;

import com.clickbait.payments.domain.model.Payment;
import java.util.Optional;

public interface ProcessPaymentUseCase {
    Payment processPayment(Payment payment);
    Optional<Payment> getPaymentById(String paymentId);
    Optional<Payment> getPaymentByOrderId(String orderId);
}