package kz.nurbolat.paymentservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.nurbolat.paymentservice.api.dto.ClientResponse;
import kz.nurbolat.paymentservice.api.dto.CreateClientRequest;
import kz.nurbolat.paymentservice.api.dto.ErrorResponse;
import kz.nurbolat.paymentservice.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clients")
@Tag(name = "Clients", description = "Client management endpoints")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create client", description = "Creates a client with required clientId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Client already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ClientResponse createClient(@Valid @RequestBody CreateClientRequest request) {
        return clientService.createClient(request);
    }

    @GetMapping
    @Operation(summary = "List clients", description = "Returns all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientResponse.class))))
    })
    public List<ClientResponse> getClients() {
        return clientService.getClients();
    }
}
