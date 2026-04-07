package kz.nurbolat.paymentservice.api;

import kz.nurbolat.paymentservice.api.dto.ClientPaymentSummaryResponse;
import kz.nurbolat.paymentservice.api.dto.CreatePaymentRequest;
import kz.nurbolat.paymentservice.api.dto.ErrorResponse;
import kz.nurbolat.paymentservice.api.dto.PaymentDetailsResponse;
import kz.nurbolat.paymentservice.api.dto.PaymentStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.nurbolat.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment", description = "Creates a new payment with PENDING status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PaymentStatusResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/payments/{paymentId}")
    @Operation(summary = "Get payment", description = "Returns payment details by paymentId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PaymentDetailsResponse getPayment(@Parameter(description = "Payment ID", required = true) @PathVariable UUID paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PostMapping("/payments/{paymentId}/confirm")
    @Operation(summary = "Confirm payment", description = "Changes payment status from PENDING to CONFIRMED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment confirmed",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PaymentStatusResponse confirmPayment(@Parameter(description = "Payment ID", required = true) @PathVariable UUID paymentId) {
        return paymentService.confirmPayment(paymentId);
    }

    @PostMapping("/payments/{paymentId}/cancel")
    @Operation(summary = "Cancel payment", description = "Changes payment status from PENDING to CANCELED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment canceled",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PaymentStatusResponse cancelPayment(@Parameter(description = "Payment ID", required = true) @PathVariable UUID paymentId) {
        return paymentService.cancelPayment(paymentId);
    }

    @GetMapping("/clients/{clientId}/payments")
    @Operation(summary = "List client payments", description = "Returns all payments for the given client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientPaymentSummaryResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ClientPaymentSummaryResponse> getClientPayments(
            @Parameter(description = "Client ID", required = true) @PathVariable String clientId) {
        return paymentService.getClientPayments(clientId);
    }
}
