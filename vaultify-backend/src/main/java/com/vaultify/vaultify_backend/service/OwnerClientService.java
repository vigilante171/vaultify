package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.ClientResponse;
import com.vaultify.vaultify_backend.dto.CreateClientRequest;
import com.vaultify.vaultify_backend.dto.UpdateClientRequest;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.Client;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerClientService {

    private final CurrentUserService currentUserService;
    private final ClientRepository clientRepository;
    private final AuditLogService auditLogService;

    public ClientResponse createClient(CreateClientRequest request) {
        User currentUser = getCurrentOwner();

        if (clientRepository.existsByBusinessIdAndPhone(currentUser.getBusinessId(), request.getPhone())) {
            throw new RuntimeException("Client phone already exists for this business");
        }

        LocalDateTime now = LocalDateTime.now();

        Client client = Client.builder()
                .businessId(currentUser.getBusinessId())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .gender(request.getGender())
                .notes(request.getNotes())
                .totalVisits(0)
                .totalSpent(0)
                .lastVisitDate(null)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Client savedClient = clientRepository.save(client);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.CREATE_CLIENT,
                "CLIENT",
                savedClient.getId(),
                "Created client " + savedClient.getFullName()
        );

        return mapToResponse(savedClient, "Client created successfully");
    }

    public List<ClientResponse> getMyClients() {
        User currentUser = getCurrentOwner();

        return clientRepository.findByBusinessId(currentUser.getBusinessId())
                .stream()
                .map(client -> mapToResponse(client, "Client loaded successfully"))
                .toList();
    }

    public List<ClientResponse> searchClients(String query) {
        User currentUser = getCurrentOwner();

        if (query == null || query.isBlank()) {
            return getMyClients();
        }

        String cleanQuery = query.trim();

        List<Client> byName = clientRepository.findByBusinessIdAndFullNameContainingIgnoreCase(
                currentUser.getBusinessId(),
                cleanQuery
        );

        List<Client> byPhone = clientRepository.findByBusinessIdAndPhoneContaining(
                currentUser.getBusinessId(),
                cleanQuery
        );

        List<Client> byEmail = clientRepository.findByBusinessIdAndEmailContainingIgnoreCase(
                currentUser.getBusinessId(),
                cleanQuery
        );

        return java.util.stream.Stream.concat(
                        java.util.stream.Stream.concat(byName.stream(), byPhone.stream()),
                        byEmail.stream()
                )
                .distinct()
                .map(client -> mapToResponse(client, "Client search result loaded successfully"))
                .toList();
    }

    public ClientResponse getClientById(String clientId) {
        User currentUser = getCurrentOwner();

        Client client = clientRepository.findByIdAndBusinessId(clientId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return mapToResponse(client, "Client loaded successfully");
    }

    public ClientResponse updateClient(String clientId, UpdateClientRequest request) {
        User currentUser = getCurrentOwner();

        Client client = clientRepository.findByIdAndBusinessId(clientId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setFullName(request.getFullName());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setGender(request.getGender());
        client.setNotes(request.getNotes());
        client.setActive(request.isActive());
        client.setUpdatedAt(LocalDateTime.now());

        Client updatedClient = clientRepository.save(client);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.UPDATE_CLIENT,
                "CLIENT",
                updatedClient.getId(),
                "Updated client " + updatedClient.getFullName()
        );

        return mapToResponse(updatedClient, "Client updated successfully");
    }

    public ClientResponse deleteClient(String clientId) {
        User currentUser = getCurrentOwner();

        Client client = clientRepository.findByIdAndBusinessId(clientId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setActive(false);
        client.setUpdatedAt(LocalDateTime.now());

        Client disabledClient = clientRepository.save(client);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.DISABLE_CLIENT,
                "CLIENT",
                disabledClient.getId(),
                "Disabled client " + disabledClient.getFullName()
        );

        return mapToResponse(disabledClient, "Client disabled successfully");
    }

    private User getCurrentOwner() {
        return currentUserService.getCurrentOwnerWithActiveBusiness();
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
