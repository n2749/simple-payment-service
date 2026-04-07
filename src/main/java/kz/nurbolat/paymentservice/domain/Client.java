package kz.nurbolat.paymentservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    protected Client() {
    }

    public Client(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
