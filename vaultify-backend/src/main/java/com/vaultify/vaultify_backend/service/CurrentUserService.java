package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.model.Business;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.BusinessRepository;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public User getCurrentOwnerWithActiveBusiness() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.OWNER) {
            throw new RuntimeException("Only business owners can access this resource");
        }

        validateUserAndBusinessAccess(currentUser);

        return currentUser;
    }

    public User getCurrentStaffWithActiveBusiness() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.STAFF) {
            throw new RuntimeException("Only staff members can access this resource");
        }

        validateUserAndBusinessAccess(currentUser);

        return currentUser;
    }

    private void validateUserAndBusinessAccess(User currentUser) {
        if (!currentUser.isActive()) {
            throw new RuntimeException("User account is disabled");
        }

        if (currentUser.getBusinessId() == null) {
            throw new RuntimeException("User is not linked to a business");
        }

        Business business = businessRepository.findById(currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (!business.isActive()) {
            throw new RuntimeException("Business is inactive");
        }

        if (business.getSubscriptionStatus() == SubscriptionStatus.SUSPENDED) {
            throw new RuntimeException("Business subscription is suspended");
        }

        if (business.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
            throw new RuntimeException("Business subscription is expired");
        }
    }
}
