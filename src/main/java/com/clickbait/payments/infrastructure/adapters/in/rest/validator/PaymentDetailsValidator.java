package com.clickbait.payments.infrastructure.adapters.in.rest.validator;

import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentDetailsDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PaymentDetailsValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PaymentDetailsDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PaymentDetailsDTO details = (PaymentDetailsDTO) target;

        if (details.getPaymentMethod() == null) {
            errors.rejectValue("paymentMethod", "paymentMethod.required", "Payment method is required");
            return;
        }

        switch (details.getPaymentMethod()) {
            case CREDIT_CARD -> validateCreditCard(details, errors);
            case PIX -> validatePix(details, errors);
            case BANK_SLIP -> validateBankSlip(details, errors);
        }
    }

    private void validateCreditCard(PaymentDetailsDTO details, Errors errors) {
        if (isBlank(details.getCardNumber())) {
            errors.rejectValue("cardNumber", "cardNumber.required", "Card number is required for credit card payment");
        }
        if (isBlank(details.getCardHolderName())) {
            errors.rejectValue("cardHolderName", "cardHolderName.required", "Card holder name is required for credit card payment");
        }
        if (isBlank(details.getExpirationDate())) {
            errors.rejectValue("expirationDate", "expirationDate.required", "Expiration date is required for credit card payment");
        }
        if (isBlank(details.getCvv())) {
            errors.rejectValue("cvv", "cvv.required", "CVV is required for credit card payment");
        }
    }

    private void validatePix(PaymentDetailsDTO details, Errors errors) {
        if (isBlank(details.getPixKey())) {
            errors.rejectValue("pixKey", "pixKey.required", "PIX key is required for PIX payment");
        }
        if (isBlank(details.getPixKeyType())) {
            errors.rejectValue("pixKeyType", "pixKeyType.required", "PIX key type is required for PIX payment");
        }
    }

    private void validateBankSlip(PaymentDetailsDTO details, Errors errors) {
        if (isBlank(details.getCustomerDocument())) {
            errors.rejectValue("customerDocument", "customerDocument.required", "Customer document is required for bank slip payment");
        }
        if (isBlank(details.getCustomerName())) {
            errors.rejectValue("customerName", "customerName.required", "Customer name is required for bank slip payment");
        }
        if (isBlank(details.getDueDate())) {
            errors.rejectValue("dueDate", "dueDate.required", "Due date is required for bank slip payment");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}