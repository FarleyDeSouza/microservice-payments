package com.clickbait.payments.infrastructure.adapters.out.persistence;

import com.clickbait.payments.domain.exception.PaymentProcessingException;
import com.clickbait.payments.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingAdapterTest {

    @InjectMocks
    private PaymentProcessingAdapter processingAdapter;

    @Test
    void shouldProcessCreditCardPayment() {
        // given
        var creditCardDetails = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("4111111111111111")
                .cardHolderName("John Doe")
                .expirationDate("12/25")
                .cvv("123")
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDetails(creditCardDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // when/then
        try {
            var result = processingAdapter.processCreditCardPayment(payment);
            assertNotNull(result);
            assertEquals(PaymentStatus.APPROVED, result.getStatus());
        } catch (PaymentProcessingException e) {
            assertEquals("Credit card payment failed", e.getMessage());
            assertEquals(PaymentStatus.REJECTED, payment.getStatus());
        }
    }

    @Test
    void shouldProcessPixPaymentSuccessfully() {
        // given
        var pixDetails = PixDetails.builder()
                .paymentMethod(PaymentMethod.PIX)
                .pixKey("test@email.com")
                .pixKeyType("EMAIL")
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.PIX)
                .status(PaymentStatus.PENDING)
                .paymentDetails(pixDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        var result = processingAdapter.processPixPayment(payment);

        // then
        assertNotNull(result);
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldProcessBankSlipPaymentSuccessfully() {
        // given
        var bankSlipDetails = BankSlipDetails.builder()
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .customerDocument("123.456.789-00")
                .customerName("John Doe")
                .dueDate(LocalDateTime.now().plusDays(3))
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .status(PaymentStatus.PENDING)
                .paymentDetails(bankSlipDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        var result = processingAdapter.processBankSlipPayment(payment);

        // then
        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertNotNull(((BankSlipDetails)result.getPaymentDetails()).getBarCode());
    }

    @Test
    void shouldThrowExceptionForInvalidPaymentMethod() {
        // given
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.PIX)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class, 
                    () -> processingAdapter.processCreditCardPayment(payment));
        assertEquals("Invalid payment method or missing payment details", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidCreditCard() {
        // given
        var creditCardDetails = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("invalid")
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDetails(creditCardDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class, 
                    () -> processingAdapter.processCreditCardPayment(payment));
        assertEquals("Invalid credit card details", exception.getMessage());
    }
}