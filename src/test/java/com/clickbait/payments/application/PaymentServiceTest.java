package com.clickbait.payments.application;

import com.clickbait.payments.domain.exception.PaymentProcessingException;
import com.clickbait.payments.domain.model.*;
import com.clickbait.payments.domain.ports.out.PaymentPersistencePort;
import com.clickbait.payments.domain.ports.out.PaymentProcessingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentPersistencePort paymentPersistencePort;

    @Mock
    private PaymentProcessingPort paymentProcessingPort;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentPersistencePort, paymentProcessingPort);
    }

    @Test
    void shouldProcessCreditCardPaymentSuccessfully() {
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
                .paymentDetails(creditCardDetails)
                .createdAt(LocalDateTime.now())
                .build();

        var processedPayment = Payment.builder()
                .id("payment123")
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDetails(payment.getPaymentDetails())
                .status(PaymentStatus.APPROVED)
                .createdAt(payment.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentProcessingPort.processCreditCardPayment(any(Payment.class))).thenReturn(processedPayment);
        when(paymentPersistencePort.savePayment(any(Payment.class))).thenReturn(processedPayment);

        // when
        var result = paymentService.processPayment(payment);

        // then
        assertNotNull(result);
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(payment.getOrderId(), result.getOrderId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(paymentProcessingPort).processCreditCardPayment(paymentCaptor.capture());
        verify(paymentPersistencePort).savePayment(paymentCaptor.capture());
    }

    @Test
    void shouldThrowExceptionWhenProcessingInvalidPayment() {
        // given
        var payment = Payment.builder()
                .orderId("order123")
                .amount(null) // explicitamente setando amount como null para melhor clareza
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class, 
            () -> paymentService.processPayment(payment));
        
        assertEquals("Payment amount must be greater than zero", exception.getMessage());
        verify(paymentProcessingPort, never()).processCreditCardPayment(any());
        verify(paymentPersistencePort, never()).savePayment(any());
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
                .paymentDetails(pixDetails)
                .createdAt(LocalDateTime.now())
                .build();

        var processedPayment = Payment.builder()
                .id("payment123")
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDetails(payment.getPaymentDetails())
                .status(PaymentStatus.APPROVED)
                .createdAt(payment.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentProcessingPort.processPixPayment(any(Payment.class))).thenReturn(processedPayment);
        when(paymentPersistencePort.savePayment(any(Payment.class))).thenReturn(processedPayment);

        // when
        var result = paymentService.processPayment(payment);

        // then
        assertNotNull(result);
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(payment.getOrderId(), result.getOrderId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(paymentProcessingPort).processPixPayment(paymentCaptor.capture());
        verify(paymentPersistencePort).savePayment(paymentCaptor.capture());
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
                .paymentDetails(bankSlipDetails)
                .createdAt(LocalDateTime.now())
                .build();

        var processedPayment = Payment.builder()
                .id("payment123")
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDetails(payment.getPaymentDetails())
                .status(PaymentStatus.PENDING)
                .createdAt(payment.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentProcessingPort.processBankSlipPayment(any(Payment.class))).thenReturn(processedPayment);
        when(paymentPersistencePort.savePayment(any(Payment.class))).thenReturn(processedPayment);

        // when
        var result = paymentService.processPayment(payment);

        // then
        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals(payment.getOrderId(), result.getOrderId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(paymentProcessingPort).processBankSlipPayment(paymentCaptor.capture());
        verify(paymentPersistencePort).savePayment(paymentCaptor.capture());
    }

    @Test
    void shouldFindPaymentById() {
        // given
        var payment = Payment.builder()
                .id("payment123")
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.APPROVED)
                .build();

        when(paymentPersistencePort.findById("payment123")).thenReturn(Optional.of(payment));

        // when
        var result = paymentService.getPaymentById("payment123");

        // then
        assertTrue(result.isPresent());
        assertEquals("payment123", result.get().getId());
    }

    @Test
    void shouldFindPaymentByOrderId() {
        // given
        var payment = Payment.builder()
                .id("payment123")
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.APPROVED)
                .build();

        when(paymentPersistencePort.findByOrderId("order123")).thenReturn(Optional.of(payment));

        // when
        var result = paymentService.getPaymentByOrderId("order123");

        // then
        assertTrue(result.isPresent());
        assertEquals("order123", result.get().getOrderId());
    }

    @Test
    void shouldThrowExceptionWhenProcessingPaymentWithInvalidCreditCardDetails() {
        // given
        var creditCardDetails = CreditCardDetails.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .cardNumber("invalid")
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentDetails(creditCardDetails)
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class,
                () -> paymentService.processPayment(payment));

        assertEquals("Card holder name must be specified", exception.getMessage());
        verify(paymentProcessingPort, never()).processCreditCardPayment(any());
        verify(paymentPersistencePort, never()).savePayment(any());
    }

    @Test
    void shouldThrowExceptionWhenProcessingPaymentWithInvalidPixDetails() {
        // given
        var pixDetails = PixDetails.builder()
                .paymentMethod(PaymentMethod.PIX)
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.PIX)
                .paymentDetails(pixDetails)
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class,
                () -> paymentService.processPayment(payment));

        assertEquals("PIX key must be specified", exception.getMessage());
        verify(paymentProcessingPort, never()).processPixPayment(any());
        verify(paymentPersistencePort, never()).savePayment(any());
    }

    @Test
    void shouldThrowExceptionWhenProcessingPaymentWithInvalidBankSlipDetails() {
        // given
        var bankSlipDetails = BankSlipDetails.builder()
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.BANK_SLIP)
                .paymentDetails(bankSlipDetails)
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class,
                () -> paymentService.processPayment(payment));

        assertEquals("Customer document must be specified for bank slip", exception.getMessage());
        verify(paymentProcessingPort, never()).processBankSlipPayment(any());
        verify(paymentPersistencePort, never()).savePayment(any());
    }

    @Test
    void shouldThrowExceptionWhenPaymentDetailsDoNotMatchPaymentMethod() {
        // given
        var pixDetails = PixDetails.builder()
                .paymentMethod(PaymentMethod.PIX)
                .pixKey("test@email.com")
                .pixKeyType("EMAIL")
                .build();

        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentDetails(pixDetails)
                .build();

        // when/then
        var exception = assertThrows(PaymentProcessingException.class,
                () -> paymentService.processPayment(payment));

        assertEquals("Payment method in details must match the payment method", exception.getMessage());
        verify(paymentProcessingPort, never()).processCreditCardPayment(any());
        verify(paymentPersistencePort, never()).savePayment(any());
    }
}