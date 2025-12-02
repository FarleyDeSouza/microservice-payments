package com.clickbait.payments.infrastructure.adapters.in.rest.dto;

import com.clickbait.payments.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(
    description = "Request object for creating a new payment",
    example = "{\"orderId\": \"order123\", \"amount\": 100.00, \"paymentMethod\": \"CREDIT_CARD\", \"paymentDetails\": {\"paymentMethod\": \"CREDIT_CARD\", \"cardNumber\": \"4111111111111111\", \"cardHolderName\": \"John Doe\", \"expirationDate\": \"12/25\", \"cvv\": \"123\"}}"
)
public class CreatePaymentRequest {
    @Schema(description = "Order ID associated with the payment", example = "order123")
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @Schema(description = "Payment amount", example = "100.00")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Schema(description = "Payment method", example = "CREDIT_CARD", allowableValues = {"CREDIT_CARD", "PIX", "BANK_SLIP"})
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment method specific details")
    private PaymentDetailsDTO paymentDetails;
}