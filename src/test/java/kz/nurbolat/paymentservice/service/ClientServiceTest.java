package kz.nurbolat.paymentservice.service;

import kz.nurbolat.paymentservice.api.dto.CreateClientRequest;
import kz.nurbolat.paymentservice.domain.Client;
import kz.nurbolat.paymentservice.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void createClientSuccess() {
        when(clientRepository.existsById("client-1")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        var response = clientService.createClient(new CreateClientRequest("client-1"));

        assertEquals("client-1", response.clientId());
    }

    @Test
    void createClientConflict() {
        when(clientRepository.existsById("client-1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> clientService.createClient(new CreateClientRequest("client-1")));
    }

    @Test
    void getClientsSuccess() {
        when(clientRepository.findAll()).thenReturn(List.of(new Client("client-1"), new Client("client-2")));

        var response = clientService.getClients();

        assertEquals(2, response.size());
        assertEquals("client-1", response.get(0).clientId());
    }
}
