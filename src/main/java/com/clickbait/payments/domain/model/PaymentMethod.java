package com.clickbait.payments.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available payment methods")
public enum PaymentMethod {
    CREDIT_CARD,
    PIX,
    BANK_SLIP
}