package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.PaginatedResponse;
import com.vaultify.vaultify_backend.dto.SuperAdminUserResponse;
import com.vaultify.vaultify_backend.dto.UpdateUserStatusRequest;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.service.SuperAdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuperAdminUserController {

    private final SuperAdminUserService superAdminUserService;

    @GetMapping("/users")
    public List<SuperAdminUserResponse> getAllUsers() {
        return superAdminUserService.getAllUsers();
    }

    @GetMapping("/users/page")
    public PaginatedResponse<SuperAdminUserResponse> getUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return superAdminUserService.getUsersPage(page, size, sortBy, direction);
    }

    @GetMapping("/users/role/{role}")
    public List<SuperAdminUserResponse> getUsersByRole(@PathVariable Role role) {
        return superAdminUserService.getUsersByRole(role);
    }

    @GetMapping("/users/{userId}")
    public SuperAdminUserResponse getUserById(@PathVariable String userId) {
        return superAdminUserService.getUserById(userId);
    }

    @GetMapping("/businesses/{businessId}/users")
    public List<SuperAdminUserResponse> getUsersByBusiness(@PathVariable String businessId) {
        return superAdminUserService.getUsersByBusiness(businessId);
    }

    @PutMapping("/users/{userId}/status")
    public SuperAdminUserResponse updateUserStatus(
            @PathVariable String userId,
            @RequestBody UpdateUserStatusRequest request
    ) {
        return superAdminUserService.updateUserStatus(userId, request);
    }
}
