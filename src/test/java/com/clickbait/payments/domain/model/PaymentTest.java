package com.clickbait.payments.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void shouldCreateValidPaymentWithCreditCard() {
        // given
        var creditCardDetails = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("4111111111111111")
                .cardHolderName("John Doe")
                .expirationDate("12/25")
                .cvv("123")
                .build();

        // when
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDetails(creditCardDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // then
        assertNotNull(payment);
        assertEquals("order123", payment.getOrderId());
        assertEquals(PaymentMethod.CREDIT_CARD, payment.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNotNull(payment.getPaymentDetails());
        assertTrue(payment.getPaymentDetails() instanceof CreditCardDetails);
    }

    @Test
    void shouldCreateValidPaymentWithPix() {
        // given
        var pixDetails = PixDetails.builder()
                .paymentMethod(PaymentMethod.PIX)
                .pixKey("test@email.com")
                .pixKeyType("EMAIL")
                .build();

        // when
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.PIX)
                .status(PaymentStatus.PENDING)
                .paymentDetails(pixDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // then
        assertNotNull(payment);
        assertEquals(PaymentMethod.PIX, payment.getPaymentMethod());
        assertTrue(payment.getPaymentDetails() instanceof PixDetails);
        var details = (PixDetails) payment.getPaymentDetails();
        assertEquals("test@email.com", details.getPixKey());
    }

    @Test
    void shouldCreateValidPaymentWithBankSlip() {
        // given
        var bankSlipDetails = BankSlipDetails.builder()
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .customerDocument("123.456.789-00")
                .customerName("John Doe")
                .dueDate(LocalDateTime.now().plusDays(3))
                .build();

        // when
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .status(PaymentStatus.PENDING)
                .paymentDetails(bankSlipDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // then
        assertNotNull(payment);
        assertEquals(PaymentMethod.BANK_SLIP, payment.getPaymentMethod());
        assertTrue(payment.getPaymentDetails() instanceof BankSlipDetails);
        var details = (BankSlipDetails) payment.getPaymentDetails();
        assertEquals("123.456.789-00", details.getCustomerDocument());
    }

    @Test
    void shouldCreatePaymentWithNegativeAmount() {
        // given/when
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("-100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .build();
        
        // then
        assertNotNull(payment);
        assertEquals(new BigDecimal("-100.00"), payment.getAmount());
    }

    @Test
    void shouldCreatePaymentWithNullOrderId() {
        // given/when
        var payment = Payment.builder()
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .build();
        
        // then
        assertNotNull(payment);
        assertNull(payment.getOrderId());
    }

    @Test
    void shouldCreatePaymentWithEmptyOrderId() {
        // given/when
        var payment = Payment.builder()
                .orderId("")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .build();
        
        // then
        assertNotNull(payment);
        assertEquals("", payment.getOrderId());
    }

    @Test
    void shouldSetDefaultStatusAsPending() {
        // given
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .createdAt(LocalDateTime.now())
                .build();

        // then
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void shouldSetCreatedAtAndUpdatedAtOnBuild() {
        // given
        var now = LocalDateTime.now();
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .createdAt(now)
                .build();

        // then
        assertEquals(now, payment.getCreatedAt());
        assertEquals(now, payment.getUpdatedAt());
    }

    @Test
    void shouldUpdateStatusAndUpdatedAt() throws InterruptedException {
        // given
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        var initialUpdatedAt = payment.getUpdatedAt();
        Thread.sleep(1); // Garantir diferença temporal

        // when
        payment.setStatus(PaymentStatus.APPROVED);

        // then
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        assertTrue(payment.getUpdatedAt().isAfter(initialUpdatedAt),
            "updatedAt should be after the initial value");
    }
}