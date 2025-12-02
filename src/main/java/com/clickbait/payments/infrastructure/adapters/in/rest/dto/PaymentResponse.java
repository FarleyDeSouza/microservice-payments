package com.clickbait.payments.infrastructure.adapters.in.rest.dto;

import com.clickbait.payments.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Response object containing payment details")
public class PaymentResponse {
    @Schema(description = "Payment unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    
    @Schema(description = "Order ID associated with the payment", example = "order123")
    private String orderId;
    
    @Schema(description = "Payment amount", example = "100.00")
    private BigDecimal amount;
    
    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;
    
    @Schema(description = "Payment status", example = "PENDING", allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED"})
    private String status;
    
    @Schema(description = "Payment creation date and time", example = "2025-10-05T14:30:00")
    private String createdAt;
    
    @Schema(description = "Payment last update date and time", example = "2025-10-05T14:30:00")
    private String updatedAt;
    
    @Schema(description = "Payment method specific details")
    private PaymentDetailsDTO paymentDetails;
}