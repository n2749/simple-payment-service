package kz.nurbolat.paymentservice.service;

import kz.nurbolat.paymentservice.api.dto.CreateClientRequest;
import kz.nurbolat.paymentservice.domain.Client;
import kz.nurbolat.paymentservice.repository.ClientRepository;
import kz.nurbolat.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
class ClientServiceTransactionTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @SpyBean
    private ClientRepository clientRepositorySpy;

    @BeforeEach
    void cleanDb() {
        Mockito.reset(clientRepositorySpy);
        paymentRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    void createClientTransactionCommit() {
        long before = clientRepository.count();

        clientService.createClient(new CreateClientRequest("client-1"));

        long after = clientRepository.count();
        assertEquals(before + 1, after);
    }

    @Test
    void createClientTransactionRollbackOnSaveFailure() {
        doThrow(new RuntimeException("db error")).when(clientRepositorySpy).save(any(Client.class));

        assertThrows(RuntimeException.class, () -> clientService.createClient(new CreateClientRequest("client-1")));

        assertEquals(0, clientRepository.count());
    }

    @Test
    void getClientsTransactionReadOnlyNoMutation() {
        clientRepository.save(new Client("client-1"));

        clientService.getClients();

        assertEquals(1, clientRepository.count());
    }

    @Test
    void createClientTransactionErrorConflict() {
        clientRepository.save(new Client("client-1"));

        assertThrows(ConflictException.class, () -> clientService.createClient(new CreateClientRequest("client-1")));

        assertEquals(1, clientRepository.count());
    }
}
