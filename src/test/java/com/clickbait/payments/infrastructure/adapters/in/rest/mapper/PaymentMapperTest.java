package com.clickbait.payments.infrastructure.adapters.in.rest.mapper;

import com.clickbait.payments.domain.model.*;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.CreatePaymentRequest;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentDetailsDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMapperTest {

    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    void shouldMapCreatePaymentRequestToPaymentEntity() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        details.setCardNumber("4111111111111111");
        details.setCardHolderName("John Doe");
        details.setExpirationDate("12/25");
        details.setCvv("123");

        var request = new CreatePaymentRequest();
        request.setOrderId("order123");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        request.setPaymentDetails(details);

        // when
        var payment = mapper.toEntity(request);

        // then
        assertNotNull(payment);
        assertEquals("order123", payment.getOrderId());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, payment.getPaymentMethod());
        assertTrue(payment.getPaymentDetails() instanceof CreditCardDetails);
        
        var creditCardDetails = (CreditCardDetails) payment.getPaymentDetails();
        assertEquals("4111111111111111", creditCardDetails.getCardNumber());
        assertEquals("John Doe", creditCardDetails.getCardHolderName());
    }

    @Test
    void shouldMapPaymentEntityToPaymentResponse() {
        // given
        var creditCardDetails = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("4111111111111111")
                .cardHolderName("John Doe")
                .expirationDate("12/25")
                .cvv("123")
                .build();

        var payment = Payment.builder()
                .id("payment123")
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .paymentDetails(creditCardDetails)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // when
        var response = mapper.toResponse(payment);

        // then
        assertNotNull(response);
        assertEquals("payment123", response.getId());
        assertEquals("order123", response.getOrderId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, response.getPaymentMethod());
        assertEquals("APPROVED", response.getStatus());
        
        var responseDetails = response.getPaymentDetails();
        assertEquals("**** **** **** 1111", responseDetails.getCardNumber()); // Verificando mascaramento
        assertEquals("John Doe", responseDetails.getCardHolderName());
    }

    @Test
    void shouldMaskCreditCardNumber() {
        // given
        var details = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("4111111111111111")
                .cardHolderName("John Doe")
                .expirationDate("12/25")
                .cvv("123")
                .build();

        var payment = Payment.builder()
                .id("payment123")
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDetails(details)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        var response = mapper.toResponse(payment);

        // then
        assertEquals("**** **** **** 1111", response.getPaymentDetails().getCardNumber());
    }
}