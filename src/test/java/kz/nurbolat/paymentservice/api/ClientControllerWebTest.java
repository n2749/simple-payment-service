package kz.nurbolat.paymentservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nurbolat.paymentservice.api.dto.ClientResponse;
import kz.nurbolat.paymentservice.api.dto.CreateClientRequest;
import kz.nurbolat.paymentservice.service.ClientService;
import kz.nurbolat.paymentservice.service.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
class ClientControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    void createClientSuccess() throws Exception {
        when(clientService.createClient(any(CreateClientRequest.class)))
                .thenReturn(new ClientResponse("client-1"));

        CreateClientRequest request = new CreateClientRequest("client-1");

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value("client-1"));
    }

    @Test
    void createClientValidationError() throws Exception {
        String body = """
                {
                  "clientId": ""
                }
                """;

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createClientConflict() throws Exception {
        when(clientService.createClient(any(CreateClientRequest.class)))
                .thenThrow(new ConflictException("Client already exists"));

        CreateClientRequest request = new CreateClientRequest("client-1");

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void listClientsSuccess() throws Exception {
        when(clientService.getClients()).thenReturn(List.of(new ClientResponse("client-1")));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value("client-1"));
    }

    @Test
    void listClientsEmpty() throws Exception {
        when(clientService.getClients()).thenReturn(List.of());

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
