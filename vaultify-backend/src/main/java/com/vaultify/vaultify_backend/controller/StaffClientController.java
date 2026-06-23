package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.ClientResponse;
import com.vaultify.vaultify_backend.service.StaffClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffClientController {

    private final StaffClientService staffClientService;

    @GetMapping
    public List<ClientResponse> getMyBusinessClients() {
        return staffClientService.getMyBusinessClients();
    }

    @GetMapping("/{clientId}")
    public ClientResponse getClientById(@PathVariable String clientId) {
        return staffClientService.getClientById(clientId);
    }
}
