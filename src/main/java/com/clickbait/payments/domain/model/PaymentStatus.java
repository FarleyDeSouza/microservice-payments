package com.clickbait.payments.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment processing status")
public enum PaymentStatus {
    APPROVED,
    REJECTED,
    PENDING
}