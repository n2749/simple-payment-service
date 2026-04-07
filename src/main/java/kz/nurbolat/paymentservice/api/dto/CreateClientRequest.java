package kz.nurbolat.paymentservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
        @NotBlank @Size(max = 64) String clientId
) {
}
