package kz.nurbolat.paymentservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private CurrencyCode currency;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Payment() {
    }

    public Payment(UUID paymentId,
                   BigDecimal amount,
                   CurrencyCode currency,
                   String description,
                   String clientId,
                   PaymentStatus status,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.clientId = clientId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getClientId() {
        return clientId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
