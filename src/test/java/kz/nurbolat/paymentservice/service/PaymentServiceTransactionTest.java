package kz.nurbolat.paymentservice.service;

import kz.nurbolat.paymentservice.api.dto.CreatePaymentRequest;
import kz.nurbolat.paymentservice.domain.Client;
import kz.nurbolat.paymentservice.domain.CurrencyCode;
import kz.nurbolat.paymentservice.domain.Payment;
import kz.nurbolat.paymentservice.domain.PaymentStatus;
import kz.nurbolat.paymentservice.repository.ClientRepository;
import kz.nurbolat.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTransactionTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @SpyBean
    private PaymentRepository paymentRepositorySpy;

    @BeforeEach
    void cleanDb() {
        Mockito.reset(paymentRepositorySpy);
        paymentRepository.deleteAll();
        clientRepository.deleteAll();
        clientRepository.save(new Client("12345"));
    }

    @Test
    void createPaymentTransactionCommit() {
        long before = paymentRepository.count();
        paymentService.createPayment(new CreatePaymentRequest(BigDecimal.valueOf(500), CurrencyCode.KZT, "ok", "12345"));
        long after = paymentRepository.count();

        assertEquals(before + 1, after);
    }

    @Test
    void createPaymentTransactionRollbackOnSaveFailure() {
        doThrow(new RuntimeException("db error")).when(paymentRepositorySpy).save(any(Payment.class));

        assertThrows(RuntimeException.class, () -> paymentService.createPayment(
                new CreatePaymentRequest(BigDecimal.valueOf(500), CurrencyCode.KZT, "ok", "12345")
        ));

        assertEquals(0, paymentRepository.count());
    }

    @Test
    void getPaymentTransactionReadOnlyNoMutation() {
        Payment payment = createPendingPayment("12345");
        paymentService.getPayment(payment.getPaymentId());

        Payment reloaded = paymentRepository.findById(payment.getPaymentId()).orElseThrow();
        assertEquals(PaymentStatus.PENDING, reloaded.getStatus());
    }

    @Test
    void getPaymentTransactionErrorNotFound() {
        assertThrows(NotFoundException.class, () -> paymentService.getPayment(UUID.randomUUID()));
    }

    @Test
    void confirmPaymentTransactionCommit() {
        Payment payment = createPendingPayment("12345");

        paymentService.confirmPayment(payment.getPaymentId());

        Payment reloaded = paymentRepository.findById(payment.getPaymentId()).orElseThrow();
        assertEquals(PaymentStatus.CONFIRMED, reloaded.getStatus());
    }

    @Test
    void confirmPaymentTransactionRollbackOnSaveFailure() {
        Payment payment = createPendingPayment("12345");
        doThrow(new RuntimeException("db error")).when(paymentRepositorySpy).save(any(Payment.class));

        assertThrows(RuntimeException.class, () -> paymentService.confirmPayment(payment.getPaymentId()));

        Payment reloaded = paymentRepository.findById(payment.getPaymentId()).orElseThrow();
        assertEquals(PaymentStatus.PENDING, reloaded.getStatus());
    }

    @Test
    void cancelPaymentTransactionCommit() {
        Payment payment = createPendingPayment("12345");

        paymentService.cancelPayment(payment.getPaymentId());

        Payment reloaded = paymentRepository.findById(payment.getPaymentId()).orElseThrow();
        assertEquals(PaymentStatus.CANCELED, reloaded.getStatus());
    }

    @Test
    void cancelPaymentTransactionRollbackOnSaveFailure() {
        Payment payment = createPendingPayment("12345");
        doThrow(new RuntimeException("db error")).when(paymentRepositorySpy).save(any(Payment.class));

        assertThrows(RuntimeException.class, () -> paymentService.cancelPayment(payment.getPaymentId()));

        Payment reloaded = paymentRepository.findById(payment.getPaymentId()).orElseThrow();
        assertEquals(PaymentStatus.PENDING, reloaded.getStatus());
    }

    @Test
    void listClientPaymentsTransactionReadOnlyNoMutation() {
        createPendingPayment("12345");

        paymentService.getClientPayments("12345");

        assertEquals(1, paymentRepository.count());
    }

    @Test
    void listClientPaymentsTransactionErrorNotFound() {
        assertThrows(NotFoundException.class, () -> paymentService.getClientPayments("missing"));
        assertEquals(0, paymentRepository.count());
    }

    private Payment createPendingPayment(String clientId) {
        return paymentRepository.save(new Payment(
                UUID.randomUUID(),
                BigDecimal.valueOf(1000),
                CurrencyCode.KZT,
                "seed",
                clientId,
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
    }
}
