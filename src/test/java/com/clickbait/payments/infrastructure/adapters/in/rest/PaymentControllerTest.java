package com.clickbait.payments.infrastructure.adapters.in.rest;

import com.clickbait.payments.domain.exception.PaymentProcessingException;
import com.clickbait.payments.domain.model.*;
import com.clickbait.payments.domain.ports.in.ProcessPaymentUseCase;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentResponse;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.CreatePaymentRequest;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentDetailsDTO;
import com.clickbait.payments.infrastructure.adapters.in.rest.mapper.PaymentMapper;
import com.clickbait.payments.infrastructure.adapters.in.rest.validator.PaymentDetailsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessPaymentUseCase processPaymentUseCase;

    @MockBean
    private PaymentMapper paymentMapper;

    @MockBean
    private PaymentDetailsValidator paymentDetailsValidator;

    @Test
    void shouldCreateCreditCardPayment() throws Exception {
        // given
        var request = new CreatePaymentRequest();
        request.setOrderId("order123");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        details.setCardNumber("4111111111111111");
        details.setCardHolderName("John Doe");
        details.setExpirationDate("12/25");
        details.setCvv("123");
        request.setPaymentDetails(details);

        var payment = Payment.builder()
                .id("payment123")
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        doNothing().when(paymentDetailsValidator).validate(any(PaymentDetailsDTO.class), any(Errors.class));
        doReturn(payment).when(paymentMapper).toEntity(any(CreatePaymentRequest.class));
        doReturn(payment).when(processPaymentUseCase).processPayment(any(Payment.class));
        doReturn(new PaymentResponse()).when(paymentMapper).toResponse(any(Payment.class));

        // when/then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
   }

    @Test
    void shouldReturnBadRequestWhenPaymentIsInvalid() throws Exception {
        // given
        var request = new CreatePaymentRequest();
        request.setOrderId("order123");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        // Missing required card details
        request.setPaymentDetails(details);

        doAnswer(invocation -> {
                Errors errors = invocation.getArgument(1);
                errors.reject("invalid.payment", "Invalid payment details");
                return null;
        }).when(paymentDetailsValidator).validate(any(PaymentDetailsDTO.class), any(Errors.class));

        // when/then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
   }

    @Test
    void shouldGetPaymentById() throws Exception {
        // given
        var payment = Payment.builder()
                .id("payment123")
                .orderId("order123")
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .build();

        doReturn(Optional.of(payment)).when(processPaymentUseCase).getPaymentById("payment123");
        doReturn(new PaymentResponse()).when(paymentMapper).toResponse(any(Payment.class));

        // when/then
        mockMvc.perform(get("/api/v1/payments/payment123"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenPaymentDoesNotExist() throws Exception {
        // given
        doReturn(Optional.empty()).when(processPaymentUseCase).getPaymentById("nonexistent");

        // when/then
        mockMvc.perform(get("/api/v1/payments/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandlePaymentProcessingException() throws Exception {
        // given
        var request = new CreatePaymentRequest();
        request.setOrderId("order123");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        details.setCardNumber("4111111111111111");
        details.setCardHolderName("John Doe");
        details.setExpirationDate("12/25");
        details.setCvv("123");
        request.setPaymentDetails(details);

        doNothing().when(paymentDetailsValidator).validate(any(PaymentDetailsDTO.class), any(Errors.class));
        doThrow(new PaymentProcessingException("Invalid payment"))
                .when(paymentMapper).toEntity(any(CreatePaymentRequest.class));

        // when/then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid payment"));
    }
}