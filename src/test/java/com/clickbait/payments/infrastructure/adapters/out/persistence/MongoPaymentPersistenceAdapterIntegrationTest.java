package com.clickbait.payments.infrastructure.adapters.out.persistence;

import com.clickbait.payments.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
@Import(MongoPaymentPersistenceAdapter.class)
@org.junit.jupiter.api.Disabled("Temporariamente desabilitado até configuração do Docker")
class MongoPaymentPersistenceAdapterIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6")
            .withExposedPorts(27017);

    @Autowired
    private MongoPaymentPersistenceAdapter persistenceAdapter;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    @Test
    void shouldSaveAndRetrievePayment() {
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
                .status(PaymentStatus.APPROVED)
                .paymentDetails(creditCardDetails)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        var savedPayment = persistenceAdapter.savePayment(payment);

        // then
        assertNotNull(savedPayment.getId());

        // when
        var retrievedPayment = persistenceAdapter.findById(savedPayment.getId());

        // then
        assertTrue(retrievedPayment.isPresent());
        assertEquals(savedPayment.getId(), retrievedPayment.get().getId());
        assertEquals(payment.getOrderId(), retrievedPayment.get().getOrderId());
        assertEquals(payment.getAmount(), retrievedPayment.get().getAmount());
        assertEquals(payment.getPaymentMethod(), retrievedPayment.get().getPaymentMethod());
        assertEquals(payment.getStatus(), retrievedPayment.get().getStatus());

        var retrievedDetails = (CreditCardDetails) retrievedPayment.get().getPaymentDetails();
        assertEquals(creditCardDetails.getCardNumber(), retrievedDetails.getCardNumber());
        assertEquals(creditCardDetails.getCardHolderName(), retrievedDetails.getCardHolderName());
    }

    @Test
    void shouldFindPaymentByOrderId() {
        // given
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.PIX)
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        persistenceAdapter.savePayment(payment);

        // when
        var found = persistenceAdapter.findByOrderId("order123");

        // then
        assertTrue(found.isPresent());
        assertEquals("order123", found.get().getOrderId());
    }

    @Test
    void shouldHandleDifferentPaymentTypes() {
        // Credit Card Payment
        var creditCardPayment = createCreditCardPayment();
        var savedCreditCard = persistenceAdapter.savePayment(creditCardPayment);
        assertNotNull(savedCreditCard.getId());
        assertTrue(savedCreditCard.getPaymentDetails() instanceof CreditCardDetails);

        // PIX Payment
        var pixPayment = createPixPayment();
        var savedPix = persistenceAdapter.savePayment(pixPayment);
        assertNotNull(savedPix.getId());
        assertTrue(savedPix.getPaymentDetails() instanceof PixDetails);

        // Bank Slip Payment
        var bankSlipPayment = createBankSlipPayment();
        var savedBankSlip = persistenceAdapter.savePayment(bankSlipPayment);
        assertNotNull(savedBankSlip.getId());
        assertTrue(savedBankSlip.getPaymentDetails() instanceof BankSlipDetails);
    }

    private Payment createCreditCardPayment() {
        var details = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("4111111111111111")
                .cardHolderName("John Doe")
                .expirationDate("12/25")
                .cvv("123")
                .build();

        return Payment.builder()
                .orderId("cc_order")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .paymentDetails(details)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createPixPayment() {
        var details = PixDetails.builder()
                .paymentMethod(PaymentMethod.PIX)
                .pixKey("test@email.com")
                .pixKeyType("EMAIL")
                .build();

        return Payment.builder()
                .orderId("pix_order")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.PIX)
                .status(PaymentStatus.APPROVED)
                .paymentDetails(details)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Payment createBankSlipPayment() {
        var details = BankSlipDetails.builder()
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .customerDocument("123.456.789-00")
                .customerName("John Doe")
                .dueDate(LocalDateTime.now().plusDays(3))
                .build();

        return Payment.builder()
                .orderId("bankslip_order")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .status(PaymentStatus.PENDING)
                .paymentDetails(details)
                .createdAt(LocalDateTime.now())
                .build();
    }
}