package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.ClientResponse;
import com.vaultify.vaultify_backend.dto.CreateClientRequest;
import com.vaultify.vaultify_backend.dto.UpdateClientRequest;
import com.vaultify.vaultify_backend.service.OwnerClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerClientController {

    private final OwnerClientService ownerClientService;

    @PostMapping
    public ClientResponse createClient(@Valid @RequestBody CreateClientRequest request) {
        return ownerClientService.createClient(request);
    }

    @GetMapping
    public List<ClientResponse> getMyClients() {
        return ownerClientService.getMyClients();
    }

    @GetMapping("/search")
    public List<ClientResponse> searchClients(@RequestParam String query) {
        return ownerClientService.searchClients(query);
    }

    @GetMapping("/{clientId}")
    public ClientResponse getClientById(@PathVariable String clientId) {
        return ownerClientService.getClientById(clientId);
    }

    @PutMapping("/{clientId}")
    public ClientResponse updateClient(
            @PathVariable String clientId,
            @Valid @RequestBody UpdateClientRequest request
    ) {
        return ownerClientService.updateClient(clientId, request);
    }

    @DeleteMapping("/{clientId}")
    public ClientResponse deleteClient(@PathVariable String clientId) {
        return ownerClientService.deleteClient(clientId);
    }
}
