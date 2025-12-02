package com.clickbait.payments.application;

import com.clickbait.payments.domain.exception.PaymentProcessingException;
import com.clickbait.payments.domain.model.BankSlipDetails;
import com.clickbait.payments.domain.model.CreditCardDetails;
import com.clickbait.payments.domain.model.Payment;
import com.clickbait.payments.domain.model.PixDetails;
import com.clickbait.payments.domain.ports.in.ProcessPaymentUseCase;
import com.clickbait.payments.domain.ports.out.PaymentPersistencePort;
import com.clickbait.payments.domain.ports.out.PaymentProcessingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService implements ProcessPaymentUseCase {

    private final PaymentPersistencePort paymentPersistencePort;
    private final PaymentProcessingPort paymentProcessingPort;

    @Override
    public Payment processPayment(Payment payment) {
        validatePayment(payment);
        
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        Payment processedPayment = switch (payment.getPaymentMethod()) {
            case CREDIT_CARD -> paymentProcessingPort.processCreditCardPayment(payment);
            case PIX -> paymentProcessingPort.processPixPayment(payment);
            case BANK_SLIP -> paymentProcessingPort.processBankSlipPayment(payment);
        };

        return paymentPersistencePort.savePayment(processedPayment);
    }

    @Override
    public Optional<Payment> getPaymentById(String paymentId) {
        return paymentPersistencePort.findById(paymentId);
    }

    @Override
    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentPersistencePort.findByOrderId(orderId);
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new PaymentProcessingException("Payment cannot be null");
        }

        if (payment.getAmount() == null || payment.getAmount().signum() <= 0) {
            throw new PaymentProcessingException("Payment amount must be greater than zero");
        }

        if (payment.getPaymentMethod() == null) {
            throw new PaymentProcessingException("Payment method must be specified");
        }

        if (payment.getOrderId() == null || payment.getOrderId().trim().isEmpty()) {
            throw new PaymentProcessingException("Order ID must be specified");
        }

        validatePaymentDetails(payment);
    }

    private void validatePaymentDetails(Payment payment) {
        if (payment.getPaymentDetails() == null) {
            throw new PaymentProcessingException("Payment details must be specified");
        }

        if (payment.getPaymentDetails().getPaymentMethod() != payment.getPaymentMethod()) {
            throw new PaymentProcessingException("Payment method in details must match the payment method");
        }

        switch (payment.getPaymentMethod()) {
            case CREDIT_CARD -> validateCreditCardDetails(payment);
            case PIX -> validatePixDetails(payment);
            case BANK_SLIP -> validateBankSlipDetails(payment);
        }
    }

    private void validateCreditCardDetails(Payment payment) {
        var details = (CreditCardDetails) payment.getPaymentDetails();
        if (details.getCardNumber() == null || details.getCardNumber().trim().isEmpty()) {
            throw new PaymentProcessingException("Card number must be specified");
        }
        if (details.getCardHolderName() == null || details.getCardHolderName().trim().isEmpty()) {
            throw new PaymentProcessingException("Card holder name must be specified");
        }
        if (details.getExpirationDate() == null || details.getExpirationDate().trim().isEmpty()) {
            throw new PaymentProcessingException("Card expiration date must be specified");
        }
        if (details.getCvv() == null || details.getCvv().trim().isEmpty()) {
            throw new PaymentProcessingException("Card CVV must be specified");
        }
    }

    private void validatePixDetails(Payment payment) {
        var details = (PixDetails) payment.getPaymentDetails();
        if (details.getPixKey() == null || details.getPixKey().trim().isEmpty()) {
            throw new PaymentProcessingException("PIX key must be specified");
        }
        if (details.getPixKeyType() == null || details.getPixKeyType().trim().isEmpty()) {
            throw new PaymentProcessingException("PIX key type must be specified");
        }
    }

    private void validateBankSlipDetails(Payment payment) {
        var details = (BankSlipDetails) payment.getPaymentDetails();
        if (details.getCustomerDocument() == null || details.getCustomerDocument().trim().isEmpty()) {
            throw new PaymentProcessingException("Customer document must be specified for bank slip");
        }
        if (details.getCustomerName() == null || details.getCustomerName().trim().isEmpty()) {
            throw new PaymentProcessingException("Customer name must be specified for bank slip");
        }
        if (details.getDueDate() == null) {
            throw new PaymentProcessingException("Due date must be specified for bank slip");
        }
        if (details.getDueDate().isBefore(LocalDateTime.now())) {
            throw new PaymentProcessingException("Due date cannot be in the past");
        }
    }
}