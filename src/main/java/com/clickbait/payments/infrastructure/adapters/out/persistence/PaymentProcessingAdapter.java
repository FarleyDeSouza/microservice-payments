package com.clickbait.payments.infrastructure.adapters.out.persistence;

import com.clickbait.payments.domain.exception.PaymentProcessingException;
import com.clickbait.payments.domain.model.*;
import com.clickbait.payments.domain.ports.out.PaymentProcessingPort;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PaymentProcessingAdapter implements PaymentProcessingPort {

    private final Random random = new Random();

    @Override
    public Payment processCreditCardPayment(Payment payment) {
        validatePaymentType(payment, PaymentMethod.CREDIT_CARD);
        
        // Simulando processamento do cartão de crédito
        if (random.nextDouble() < 0.1) { // 10% de chance de falha
            payment.setStatus(PaymentStatus.REJECTED);
            throw new PaymentProcessingException("Credit card payment failed");
        }
        
        payment.setStatus(PaymentStatus.APPROVED);
        return payment;
    }

    @Override
    public Payment processPixPayment(Payment payment) {
        validatePaymentType(payment, PaymentMethod.PIX);
        
        // Simulando processamento do PIX
        if (random.nextDouble() < 0.05) { // 5% de chance de falha
            payment.setStatus(PaymentStatus.REJECTED);
            throw new PaymentProcessingException("PIX payment failed");
        }
        
        payment.setStatus(PaymentStatus.APPROVED);
        return payment;
    }

    @Override
    public Payment processBankSlipPayment(Payment payment) {
        validatePaymentType(payment, PaymentMethod.BANK_SLIP);
        
        // Boleto sempre começa como pendente
        payment.setStatus(PaymentStatus.PENDING);
        
        // Gerar código de barras simulado
        var details = (BankSlipDetails) payment.getPaymentDetails();
        if (details.getBarCode() == null || details.getBarCode().trim().isEmpty()) {
            details.setBarCode(generateBarCode());
        }
        
        return payment;
    }

    private void validatePaymentType(Payment payment, PaymentMethod expectedMethod) {
        if (payment.getPaymentMethod() != expectedMethod || payment.getPaymentDetails() == null) {
            throw new PaymentProcessingException("Invalid payment method or missing payment details");
        }
        
        // Validate credit card details
        if (expectedMethod == PaymentMethod.CREDIT_CARD) {
            var details = (CreditCardDetails) payment.getPaymentDetails();
            if (details.getCardNumber() == null || !details.getCardNumber().matches("\\d{16}")) {
                throw new PaymentProcessingException("Invalid credit card details");
            }
        }
    }

    private String generateBarCode() {
        // Simulando geração de código de barras (formato simplificado)
        StringBuilder barCode = new StringBuilder();
        for (int i = 0; i < 48; i++) {
            barCode.append(random.nextInt(10));
        }
        return barCode.toString();
    }
}