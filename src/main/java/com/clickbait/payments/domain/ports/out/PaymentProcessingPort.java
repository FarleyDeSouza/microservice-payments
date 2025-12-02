package com.clickbait.payments.domain.ports.out;

import com.clickbait.payments.domain.model.Payment;

public interface PaymentProcessingPort {
    Payment processCreditCardPayment(Payment payment);
    Payment processPixPayment(Payment payment);
    Payment processBankSlipPayment(Payment payment);
}