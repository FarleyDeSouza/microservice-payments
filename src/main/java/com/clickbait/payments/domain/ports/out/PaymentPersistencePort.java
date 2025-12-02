package com.clickbait.payments.domain.ports.out;

import com.clickbait.payments.domain.model.Payment;
import java.util.Optional;

public interface PaymentPersistencePort {
    Payment savePayment(Payment payment);
    Optional<Payment> findById(String id);
    Optional<Payment> findByOrderId(String orderId);
}