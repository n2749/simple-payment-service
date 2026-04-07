package kz.nurbolat.paymentservice.service;

import kz.nurbolat.paymentservice.api.dto.CreatePaymentRequest;
import kz.nurbolat.paymentservice.domain.CurrencyCode;
import kz.nurbolat.paymentservice.domain.Payment;
import kz.nurbolat.paymentservice.domain.PaymentStatus;
import kz.nurbolat.paymentservice.repository.ClientRepository;
import kz.nurbolat.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID paymentId;
    private Payment pendingPayment;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        pendingPayment = new Payment(
                paymentId,
                BigDecimal.valueOf(1000),
                CurrencyCode.KZT,
                "Order 1",
                "12345",
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createPaymentSuccess() {
        CreatePaymentRequest request = new CreatePaymentRequest(BigDecimal.valueOf(100), CurrencyCode.USD, "d", "12345");
        when(clientRepository.existsById("12345")).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        var response = paymentService.createPayment(request);

        assertEquals(PaymentStatus.PENDING, response.status());
    }

    @Test
    void createPaymentNotFoundClient() {
        CreatePaymentRequest request = new CreatePaymentRequest(BigDecimal.valueOf(100), CurrencyCode.USD, "d", "missing");
        when(clientRepository.existsById("missing")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> paymentService.createPayment(request));
    }

    @Test
    void getPaymentSuccess() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(pendingPayment));

        var response = paymentService.getPayment(paymentId);

        assertEquals(paymentId, response.paymentId());
        assertEquals(PaymentStatus.PENDING, response.status());
    }

    @Test
    void getPaymentNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.getPayment(paymentId));
    }

    @Test
    void confirmPaymentSuccess() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        var response = paymentService.confirmPayment(paymentId);

        assertEquals(PaymentStatus.CONFIRMED, response.status());
    }

    @Test
    void confirmPaymentInvalidState() {
        Payment confirmed = new Payment(
                paymentId,
                BigDecimal.valueOf(100),
                CurrencyCode.USD,
                "desc",
                "12345",
                PaymentStatus.CONFIRMED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(confirmed));

        assertThrows(InvalidOperationException.class, () -> paymentService.confirmPayment(paymentId));
    }

    @Test
    void cancelPaymentSuccess() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        var response = paymentService.cancelPayment(paymentId);

        assertEquals(PaymentStatus.CANCELED, response.status());
    }

    @Test
    void cancelPaymentInvalidState() {
        Payment canceled = new Payment(
                paymentId,
                BigDecimal.valueOf(100),
                CurrencyCode.USD,
                "desc",
                "12345",
                PaymentStatus.CANCELED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(canceled));

        assertThrows(InvalidOperationException.class, () -> paymentService.cancelPayment(paymentId));
    }

    @Test
    void getClientPaymentsSuccess() {
        when(clientRepository.existsById("12345")).thenReturn(true);
        when(paymentRepository.findAllByClientId("12345")).thenReturn(List.of(pendingPayment));

        var response = paymentService.getClientPayments("12345");

        assertEquals(1, response.size());
        assertEquals(PaymentStatus.PENDING, response.getFirst().status());
    }

    @Test
    void getClientPaymentsNotFoundClient() {
        when(clientRepository.existsById("missing")).thenReturn(false);
        assertThrows(NotFoundException.class, () -> paymentService.getClientPayments("missing"));
    }
}
