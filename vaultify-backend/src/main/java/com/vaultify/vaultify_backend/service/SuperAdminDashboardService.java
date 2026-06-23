package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.SuperAdminDashboardResponse;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.BusinessRepository;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminDashboardService {

    private final CurrentUserService currentUserService;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public SuperAdminDashboardResponse getDashboard() {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new RuntimeException("Only super admin can access this resource");
        }

        if (!currentUser.isActive()) {
            throw new RuntimeException("Super admin account is disabled");
        }

        long totalBusinesses = businessRepository.count();
        long activeBusinesses = businessRepository.countByActive(true);
        long inactiveBusinesses = businessRepository.countByActive(false);

        long trialBusinesses = businessRepository.countBySubscriptionStatus(SubscriptionStatus.TRIAL);
        long activeSubscriptions = businessRepository.countBySubscriptionStatus(SubscriptionStatus.ACTIVE);
        long expiredSubscriptions = businessRepository.countBySubscriptionStatus(SubscriptionStatus.EXPIRED);
        long suspendedSubscriptions = businessRepository.countBySubscriptionStatus(SubscriptionStatus.SUSPENDED);

        long totalOwners = userRepository.countByRole(Role.OWNER);
        long totalStaff = userRepository.countByRole(Role.STAFF);

        return SuperAdminDashboardResponse.builder()
                .totalBusinesses(totalBusinesses)
                .activeBusinesses(activeBusinesses)
                .inactiveBusinesses(inactiveBusinesses)
                .trialBusinesses(trialBusinesses)
                .activeSubscriptions(activeSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .suspendedSubscriptions(suspendedSubscriptions)
                .totalOwners(totalOwners)
                .totalStaff(totalStaff)
                .message("Super admin dashboard loaded successfully")
                .build();
    }
}
