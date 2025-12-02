package com.clickbait.payments.infrastructure.adapters.out.persistence;

import com.clickbait.payments.domain.model.Payment;
import com.clickbait.payments.domain.ports.out.PaymentPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MongoPaymentPersistenceAdapter implements PaymentPersistencePort {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}