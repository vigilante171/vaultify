package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.ClientResponse;
import com.vaultify.vaultify_backend.model.Client;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffClientService {

    private final CurrentUserService currentUserService;
    private final ClientRepository clientRepository;

    public List<ClientResponse> getMyBusinessClients() {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        return clientRepository.findByBusinessIdAndActive(staff.getBusinessId(), true)
                .stream()
                .map(client -> mapToResponse(client, "Client loaded successfully"))
                .toList();
    }

    public ClientResponse getClientById(String clientId) {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        Client client = clientRepository.findByIdAndBusinessId(clientId, staff.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (!client.isActive()) {
            throw new RuntimeException("Client is inactive");
        }

        return mapToResponse(client, "Client loaded successfully");
    }

    private ClientResponse mapToResponse(Client client, String message) {
        return ClientResponse.builder()
                .clientId(client.getId())
                .businessId(client.getBusinessId())
                .fullName(client.getFullName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .gender(client.getGender())
                .notes(client.getNotes())
                .totalVisits(client.getTotalVisits())
                .totalSpent(client.getTotalSpent())
                .lastVisitDate(client.getLastVisitDate())
                .active(client.isActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .message(message)
                .build();
    }
}
