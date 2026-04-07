package kz.nurbolat.paymentservice.api.dto;

import kz.nurbolat.paymentservice.domain.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull CurrencyCode currency,
        String description,
        @NotBlank String clientId
) {
}
