package com.clickbait.payments.infrastructure.adapters.in.rest.dto;

import com.clickbait.payments.domain.model.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "Payment method specific details",
    oneOf = {Schema.class, Schema.class, Schema.class}
)
public class PaymentDetailsDTO {
    @Schema(description = "Payment method type", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    // Campos para cartão de crédito
    @Schema(description = "Credit card number (required for CREDIT_CARD)", example = "4111111111111111")
    private String cardNumber;
    
    @Schema(description = "Name of the card holder (required for CREDIT_CARD)", example = "John Doe")
    private String cardHolderName;
    
    @Schema(description = "Card expiration date", example = "12/25")
    private String expirationDate;
    
    @Schema(description = "Card security code", example = "123")
    private String cvv;

    // Campos para PIX
    @Schema(description = "PIX key", example = "123e4567-e89b-12d3-a456-426614174000")
    private String pixKey;
    
    @Schema(description = "PIX key type", example = "random", allowableValues = {"cpf", "cnpj", "email", "phone", "random"})
    private String pixKeyType;

    // Campos para boleto
    @Schema(description = "Customer document (CPF/CNPJ)", example = "123.456.789-00")
    private String customerDocument;
    
    @Schema(description = "Customer full name", example = "John Doe")
    private String customerName;
    
    @Schema(description = "Bank slip bar code", example = "34191.79001 01043.510047 91020.150008 1 91690026000")
    private String barCode;
    
    @Schema(description = "Bank slip due date", example = "2025-12-31T23:59:59")
    private String dueDate;
}