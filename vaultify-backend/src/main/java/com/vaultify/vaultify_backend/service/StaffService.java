package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.StaffDashboardResponse;
import com.vaultify.vaultify_backend.model.Business;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final CurrentUserService currentUserService;
    private final BusinessRepository businessRepository;

    public StaffDashboardResponse getMyDashboard() {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        Business business = businessRepository.findById(staff.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        return StaffDashboardResponse.builder()
                .staffId(staff.getId())
                .businessId(staff.getBusinessId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .role(staff.getRole())
                .active(staff.isActive())
                .businessName(business.getName())
                .businessType(business.getType())
                .businessPhone(business.getPhone())
                .businessAddress(business.getAddress())
                .message("Staff dashboard loaded successfully")
                .build();
    }
}
