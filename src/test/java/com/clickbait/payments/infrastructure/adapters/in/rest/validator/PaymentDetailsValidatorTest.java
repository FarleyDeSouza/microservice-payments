package com.clickbait.payments.infrastructure.adapters.in.rest.validator;

import com.clickbait.payments.domain.model.PaymentMethod;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentDetailsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDetailsValidatorTest {

    private final PaymentDetailsValidator validator = new PaymentDetailsValidator();

    @Test
    void shouldValidateCreditCardPayment() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        details.setCardNumber("4111111111111111");
        details.setCardHolderName("John Doe");
        details.setExpirationDate("12/25");
        details.setCvv("123");

        var errors = new BeanPropertyBindingResult(details, "paymentDetails");

        // when
        validator.validate(details, errors);

        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    void shouldValidatePixPayment() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.PIX);
        details.setPixKey("123e4567-e89b-12d3-a456-426614174000");
        details.setPixKeyType("random");

        var errors = new BeanPropertyBindingResult(details, "paymentDetails");

        // when
        validator.validate(details, errors);

        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    void shouldValidateBankSlipPayment() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.BANK_SLIP);
        details.setCustomerDocument("123.456.789-00");
        details.setCustomerName("John Doe");
        details.setDueDate("2025-12-31T23:59:59");

        var errors = new BeanPropertyBindingResult(details, "paymentDetails");

        // when
        validator.validate(details, errors);

        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    void shouldRejectCreditCardPaymentWithoutCardNumber() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        details.setCardHolderName("John Doe");
        details.setExpirationDate("12/25");
        details.setCvv("123");

        var errors = new BeanPropertyBindingResult(details, "paymentDetails");

        // when
        validator.validate(details, errors);

        // then
        assertTrue(errors.hasErrors());
        assertEquals("Card number is required for credit card payment", 
                    errors.getFieldError("cardNumber").getDefaultMessage());
    }

    @Test
    void shouldRejectPixPaymentWithoutPixKey() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.PIX);
        details.setPixKeyType("random");

        var errors = new BeanPropertyBindingResult(details, "paymentDetails");

        // when
        validator.validate(details, errors);

        // then
        assertTrue(errors.hasErrors());
        assertEquals("PIX key is required for PIX payment", 
                    errors.getFieldError("pixKey").getDefaultMessage());
    }

    @Test
    void shouldRejectBankSlipPaymentWithoutCustomerDocument() {
        // given
        var details = new PaymentDetailsDTO();
        details.setPaymentMethod(PaymentMethod.BANK_SLIP);
        details.setCustomerName("John Doe");
        details.setDueDate("2025-12-31T23:59:59");

        var errors = new BeanPropertyBindingResult(details, "paymentDetails");

        // when
        validator.validate(details, errors);

        // then
        assertTrue(errors.hasErrors());
        assertEquals("Customer document is required for bank slip payment", 
                    errors.getFieldError("customerDocument").getDefaultMessage());
    }
}