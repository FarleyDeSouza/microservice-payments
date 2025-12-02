package com.clickbait.payments.infrastructure.adapters.out.persistence;

import com.clickbait.payments.domain.model.Payment;
import com.clickbait.payments.domain.model.PaymentMethod;
import com.clickbait.payments.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoPaymentPersistenceAdapterTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private MongoPaymentPersistenceAdapter persistenceAdapter;

    @Test
    void shouldSavePayment() {
        // given
        var payment = Payment.builder()
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.APPROVED)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        var savedPayment = persistenceAdapter.savePayment(payment);

        // then
        assertNotNull(savedPayment);
        verify(paymentRepository).save(payment);
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

        when(paymentRepository.findById("payment123")).thenReturn(Optional.of(payment));

        // when
        var found = persistenceAdapter.findById("payment123");

        // then
        assertTrue(found.isPresent());
        assertEquals("payment123", found.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenPaymentNotFound() {
        // given
        when(paymentRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // when
        var result = persistenceAdapter.findById("nonexistent");

        // then
        assertTrue(result.isEmpty());
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

        when(paymentRepository.findByOrderId("order123")).thenReturn(Optional.of(payment));

        // when
        var found = persistenceAdapter.findByOrderId("order123");

        // then
        assertTrue(found.isPresent());
        assertEquals("order123", found.get().getOrderId());
    }
}