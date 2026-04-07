package kz.nurbolat.paymentservice.service;

import kz.nurbolat.paymentservice.api.dto.ClientResponse;
import kz.nurbolat.paymentservice.api.dto.CreateClientRequest;
import kz.nurbolat.paymentservice.domain.Client;
import kz.nurbolat.paymentservice.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        if (clientRepository.existsById(request.clientId())) {
            throw new ConflictException("Client already exists: " + request.clientId());
        }
        Client saved = clientRepository.save(new Client(request.clientId()));
        return new ClientResponse(saved.getClientId());
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getClients() {
        return clientRepository.findAll()
                .stream()
                .map(client -> new ClientResponse(client.getClientId()))
                .toList();
    }
}
