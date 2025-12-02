package com.clickbait.payments.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreditCardDetails extends PaymentDetails {
    private String cardNumber;
    private String cardHolderName;
    private String expirationDate;
    private String cvv;
}