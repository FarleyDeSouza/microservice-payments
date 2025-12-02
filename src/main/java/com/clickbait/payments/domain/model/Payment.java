package com.clickbait.payments.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentDetails paymentDetails;

    @Builder
    public Payment(String id, String orderId, BigDecimal amount, PaymentMethod paymentMethod, 
                  PaymentStatus status, PaymentDetails paymentDetails, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.paymentDetails = paymentDetails;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        LocalDateTime now = LocalDateTime.now();
        if (this.updatedAt == null) {
            this.updatedAt = now;
        } else {
            this.updatedAt = now;
        }
    }
}