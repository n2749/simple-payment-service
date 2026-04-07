package kz.nurbolat.paymentservice.repository;

import kz.nurbolat.paymentservice.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, String> {
}
