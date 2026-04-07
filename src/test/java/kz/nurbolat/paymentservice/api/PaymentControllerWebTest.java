package kz.nurbolat.paymentservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nurbolat.paymentservice.api.dto.ClientPaymentSummaryResponse;
import kz.nurbolat.paymentservice.api.dto.CreatePaymentRequest;
import kz.nurbolat.paymentservice.api.dto.PaymentDetailsResponse;
import kz.nurbolat.paymentservice.api.dto.PaymentStatusResponse;
import kz.nurbolat.paymentservice.domain.CurrencyCode;
import kz.nurbolat.paymentservice.domain.PaymentStatus;
import kz.nurbolat.paymentservice.service.InvalidOperationException;
import kz.nurbolat.paymentservice.service.NotFoundException;
import kz.nurbolat.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    void createPaymentSuccess() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.createPayment(any(CreatePaymentRequest.class)))
                .thenReturn(new PaymentStatusResponse(paymentId, PaymentStatus.PENDING));

        CreatePaymentRequest request = new CreatePaymentRequest(BigDecimal.valueOf(100), CurrencyCode.KZT, "Order", "12345");

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createPaymentInvalidCurrency() throws Exception {
        String body = """
                {
                  "amount": 100,
                  "currency": "GBP",
                  "description": "Order",
                  "clientId": "12345"
                }
                """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createPaymentValidationError() throws Exception {
        String body = """
                {
                  "amount": -1,
                  "currency": "KZT",
                  "description": "Order",
                  "clientId": "12345"
                }
                """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getPaymentSuccess() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.getPayment(paymentId)).thenReturn(new PaymentDetailsResponse(
                paymentId,
                BigDecimal.valueOf(100),
                CurrencyCode.USD,
                "Order",
                "12345",
                PaymentStatus.CONFIRMED
        ));

        mockMvc.perform(get("/payments/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getPaymentNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.getPayment(paymentId)).thenThrow(new NotFoundException("Payment not found"));

        mockMvc.perform(get("/payments/{paymentId}", paymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmPaymentSuccess() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.confirmPayment(paymentId)).thenReturn(new PaymentStatusResponse(paymentId, PaymentStatus.CONFIRMED));

        mockMvc.perform(post("/payments/{paymentId}/confirm", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmPaymentInvalidState() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.confirmPayment(paymentId)).thenThrow(new InvalidOperationException("invalid"));

        mockMvc.perform(post("/payments/{paymentId}/confirm", paymentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelPaymentSuccess() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.cancelPayment(paymentId)).thenReturn(new PaymentStatusResponse(paymentId, PaymentStatus.CANCELED));

        mockMvc.perform(post("/payments/{paymentId}/cancel", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    void cancelPaymentNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.cancelPayment(paymentId)).thenThrow(new NotFoundException("Payment not found"));

        mockMvc.perform(post("/payments/{paymentId}/cancel", paymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listClientPaymentsSuccess() throws Exception {
        when(paymentService.getClientPayments(eq("12345"))).thenReturn(List.of(
                new ClientPaymentSummaryResponse(UUID.randomUUID(), BigDecimal.valueOf(100), CurrencyCode.KZT, PaymentStatus.PENDING)
        ));

        mockMvc.perform(get("/clients/{clientId}/payments", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void listClientPaymentsNotFound() throws Exception {
        when(paymentService.getClientPayments(eq("missing"))).thenThrow(new NotFoundException("Client not found"));

        mockMvc.perform(get("/clients/{clientId}/payments", "missing"))
                .andExpect(status().isNotFound());
    }
}
