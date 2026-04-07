package kz.nurbolat.paymentservice.service;

import kz.nurbolat.paymentservice.api.dto.ClientPaymentSummaryResponse;
import kz.nurbolat.paymentservice.api.dto.CreatePaymentRequest;
import kz.nurbolat.paymentservice.api.dto.PaymentDetailsResponse;
import kz.nurbolat.paymentservice.api.dto.PaymentStatusResponse;
import kz.nurbolat.paymentservice.domain.Payment;
import kz.nurbolat.paymentservice.domain.PaymentStatus;
import kz.nurbolat.paymentservice.repository.ClientRepository;
import kz.nurbolat.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;

    public PaymentService(PaymentRepository paymentRepository, ClientRepository clientRepository) {
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public PaymentStatusResponse createPayment(CreatePaymentRequest request) {
        ensureClientExists(request.clientId());
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(
                UUID.randomUUID(),
                request.amount(),
                request.currency(),
                request.description(),
                request.clientId(),
                PaymentStatus.PENDING,
                now,
                now
        );
        Payment saved = paymentRepository.save(payment);
        return new PaymentStatusResponse(saved.getPaymentId(), saved.getStatus());
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPayment(UUID paymentId) {
        Payment payment = getPaymentEntity(paymentId);
        return new PaymentDetailsResponse(
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                payment.getClientId(),
                payment.getStatus()
        );
    }

    @Transactional
    public PaymentStatusResponse confirmPayment(UUID paymentId) {
        Payment payment = getPaymentEntity(paymentId);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Only pending payments can be confirmed");
        }
        payment.setStatus(PaymentStatus.CONFIRMED);
        Payment saved = paymentRepository.save(payment);
        return new PaymentStatusResponse(saved.getPaymentId(), saved.getStatus());
    }

    @Transactional
    public PaymentStatusResponse cancelPayment(UUID paymentId) {
        Payment payment = getPaymentEntity(paymentId);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Only pending payments can be canceled");
        }
        payment.setStatus(PaymentStatus.CANCELED);
        Payment saved = paymentRepository.save(payment);
        return new PaymentStatusResponse(saved.getPaymentId(), saved.getStatus());
    }

    @Transactional(readOnly = true)
    public List<ClientPaymentSummaryResponse> getClientPayments(String clientId) {
        ensureClientExists(clientId);
        return paymentRepository.findAllByClientId(clientId)
                .stream()
                .map(payment -> new ClientPaymentSummaryResponse(
                        payment.getPaymentId(),
                        payment.getAmount(),
                        payment.getCurrency(),
                        payment.getStatus()
                ))
                .toList();
    }

    private void ensureClientExists(String clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new NotFoundException("Client not found: " + clientId);
        }
    }

    private Payment getPaymentEntity(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
    }
}
