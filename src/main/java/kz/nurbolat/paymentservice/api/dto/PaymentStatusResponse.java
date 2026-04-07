package kz.nurbolat.paymentservice.api.dto;

import kz.nurbolat.paymentservice.domain.PaymentStatus;

import java.util.UUID;

public record PaymentStatusResponse(UUID paymentId, PaymentStatus status) {
}
