package kz.nurbolat.paymentservice.api.dto;

import kz.nurbolat.paymentservice.domain.CurrencyCode;
import kz.nurbolat.paymentservice.domain.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDetailsResponse(
        UUID paymentId,
        BigDecimal amount,
        CurrencyCode currency,
        String description,
        String clientId,
        PaymentStatus status
) {
}
