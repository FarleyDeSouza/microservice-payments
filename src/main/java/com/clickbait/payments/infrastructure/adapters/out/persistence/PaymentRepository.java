package com.clickbait.payments.infrastructure.adapters.out.persistence;

import com.clickbait.payments.domain.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
}