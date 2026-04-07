package kz.nurbolat.paymentservice.api.dto;

import kz.nurbolat.paymentservice.domain.CurrencyCode;
import kz.nurbolat.paymentservice.domain.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientPaymentSummaryResponse(
        UUID paymentId,
        BigDecimal amount,
        CurrencyCode currency,
        PaymentStatus status
) {
}
