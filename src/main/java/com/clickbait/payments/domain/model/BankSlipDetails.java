package com.clickbait.payments.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BankSlipDetails extends PaymentDetails {
    private String customerDocument;
    private String customerName;
    private String barCode;
    private LocalDateTime dueDate;
}