package com.clickbait.payments.infrastructure.adapters.in.rest.mapper;

import com.clickbait.payments.domain.model.*;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.CreatePaymentRequest;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentDetailsDTO;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentMapper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public Payment toEntity(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDetails(toPaymentDetails(request.getPaymentDetails()));
        return payment;
    }

    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus().name());
        response.setCreatedAt(formatDateTime(payment.getCreatedAt()));
        response.setUpdatedAt(formatDateTime(payment.getUpdatedAt()));
        response.setPaymentDetails(toPaymentDetailsDTO(payment.getPaymentDetails()));
        return response;
    }

    private PaymentDetails toPaymentDetails(PaymentDetailsDTO dto) {
        if (dto == null) return null;

        PaymentMethod method = dto.getPaymentMethod();
        return switch (method) {
            case CREDIT_CARD -> CreditCardDetails.builder()
                    .paymentMethod(method)
                    .cardNumber(dto.getCardNumber())
                    .cardHolderName(dto.getCardHolderName())
                    .expirationDate(dto.getExpirationDate())
                    .cvv(dto.getCvv())
                    .build();
            case PIX -> PixDetails.builder()
                    .paymentMethod(method)
                    .pixKey(dto.getPixKey())
                    .pixKeyType(dto.getPixKeyType())
                    .build();
            case BANK_SLIP -> BankSlipDetails.builder()
                    .paymentMethod(method)
                    .customerDocument(dto.getCustomerDocument())
                    .customerName(dto.getCustomerName())
                    .barCode(dto.getBarCode())
                    .dueDate(dto.getDueDate() != null ? LocalDateTime.parse(dto.getDueDate(), DATE_FORMATTER) : null)
                    .build();
        };
    }

    private PaymentDetailsDTO toPaymentDetailsDTO(PaymentDetails details) {
        if (details == null) return null;

        PaymentDetailsDTO dto = new PaymentDetailsDTO();
        dto.setPaymentMethod(details.getPaymentMethod());

        switch (details.getPaymentMethod()) {
            case CREDIT_CARD -> {
                CreditCardDetails creditCard = (CreditCardDetails) details;
                dto.setCardNumber(maskCardNumber(creditCard.getCardNumber()));
                dto.setCardHolderName(creditCard.getCardHolderName());
                dto.setExpirationDate(creditCard.getExpirationDate());
            }
            case PIX -> {
                PixDetails pix = (PixDetails) details;
                dto.setPixKey(pix.getPixKey());
                dto.setPixKeyType(pix.getPixKeyType());
            }
            case BANK_SLIP -> {
                BankSlipDetails bankSlip = (BankSlipDetails) details;
                dto.setCustomerDocument(bankSlip.getCustomerDocument());
                dto.setCustomerName(bankSlip.getCustomerName());
                dto.setBarCode(bankSlip.getBarCode());
                dto.setDueDate(formatDateTime(bankSlip.getDueDate()));
            }
        }

        return dto;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}