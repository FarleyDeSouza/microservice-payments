package com.clickbait.payments.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.clickbait.payments.infrastructure.adapters.in.rest.validator.PaymentDetailsValidator;

import com.clickbait.payments.domain.exception.PaymentProcessingException;
import com.clickbait.payments.domain.ports.in.ProcessPaymentUseCase;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.CreatePaymentRequest;
import com.clickbait.payments.infrastructure.adapters.in.rest.dto.PaymentResponse;
import com.clickbait.payments.infrastructure.adapters.in.rest.mapper.PaymentMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Endpoints for payment processing")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentMapper paymentMapper;
    private final PaymentDetailsValidator paymentDetailsValidator;

    @Operation(summary = "Create a new payment", description = "Process a new payment with the provided payment details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment data provided")
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        // Validar os detalhes do pagamento
        if (request.getPaymentDetails() != null) {
            var errors = new org.springframework.validation.BeanPropertyBindingResult(
                request.getPaymentDetails(), "paymentDetails");
            paymentDetailsValidator.validate(request.getPaymentDetails(), errors);
            if (errors.hasErrors()) {
                throw new IllegalArgumentException(errors.getFieldError().getDefaultMessage());
            }
        }

        var payment = paymentMapper.toEntity(request);
        var processedPayment = processPaymentUseCase.processPayment(payment);
        var response = paymentMapper.toResponse(processedPayment);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(processedPayment.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String id) {
        return processPaymentUseCase.getPaymentById(id)
                .map(payment -> ResponseEntity.ok(paymentMapper.toResponse(payment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get payment by order ID", description = "Retrieve payment details by its associated order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
        return processPaymentUseCase.getPaymentByOrderId(orderId)
                .map(payment -> ResponseEntity.ok(paymentMapper.toResponse(payment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler({PaymentProcessingException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handlePaymentExceptions(Exception ex) {
        log.warn("Payment validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    private record ErrorResponse(String message) {}
}
