package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.OwnerDashboardResponse;
import com.vaultify.vaultify_backend.dto.UpdateBusinessProfileRequest;
import com.vaultify.vaultify_backend.model.Appointment;
import com.vaultify.vaultify_backend.model.AppointmentStatus;
import com.vaultify.vaultify_backend.model.Business;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.AppointmentRepository;
import com.vaultify.vaultify_backend.repository.BusinessRepository;
import com.vaultify.vaultify_backend.repository.ClientRepository;
import com.vaultify.vaultify_backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final CurrentUserService currentUserService;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;

    public OwnerDashboardResponse getMyBusinessDashboard() {
        User currentUser = currentUserService.getCurrentOwnerWithActiveBusiness();

        Business business = businessRepository.findById(currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        long totalServices = serviceRepository.countByBusinessIdAndActive(business.getId(), true);
        long totalClients = clientRepository.countByBusinessIdAndActive(business.getId(), true);
        long totalAppointments = appointmentRepository.countByBusinessIdAndActive(business.getId(), true);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

        double monthlyRevenue = appointmentRepository
                .findByBusinessIdAndStatusAndAppointmentDateTimeBetween(
                        business.getId(),
                        AppointmentStatus.COMPLETED,
                        startOfMonth,
                        startOfNextMonth
                )
                .stream()
                .filter(Appointment::isActive)
                .mapToDouble(Appointment::getPrice)
                .sum();

        return mapToOwnerDashboardResponse(
                currentUser,
                business,
                totalClients,
                totalServices,
                totalAppointments,
                monthlyRevenue,
                "Owner dashboard loaded successfully"
        );
    }

    public OwnerDashboardResponse updateMyBusinessProfile(UpdateBusinessProfileRequest request) {
        User currentUser = currentUserService.getCurrentOwnerWithActiveBusiness();

        Business business = businessRepository.findById(currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setName(request.getBusinessName());
        business.setPhone(request.getPhone());
        business.setAddress(request.getAddress());
        business.setLogoUrl(request.getLogoUrl());
        business.setUpdatedAt(LocalDateTime.now());

        Business updatedBusiness = businessRepository.save(business);

        long totalServices = serviceRepository.countByBusinessIdAndActive(updatedBusiness.getId(), true);
        long totalClients = clientRepository.countByBusinessIdAndActive(updatedBusiness.getId(), true);
        long totalAppointments = appointmentRepository.countByBusinessIdAndActive(updatedBusiness.getId(), true);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

        double monthlyRevenue = appointmentRepository
                .findByBusinessIdAndStatusAndAppointmentDateTimeBetween(
                        updatedBusiness.getId(),
                        AppointmentStatus.COMPLETED,
                        startOfMonth,
                        startOfNextMonth
                )
                .stream()
                .filter(Appointment::isActive)
                .mapToDouble(Appointment::getPrice)
                .sum();

        return mapToOwnerDashboardResponse(
                currentUser,
                updatedBusiness,
                totalClients,
                totalServices,
                totalAppointments,
                monthlyRevenue,
                "Business profile updated successfully"
        );
    }

    private OwnerDashboardResponse mapToOwnerDashboardResponse(
            User owner,
            Business business,
            long totalClients,
            long totalServices,
            long totalAppointments,
            double monthlyRevenue,
            String message
    ) {
        return OwnerDashboardResponse.builder()
                .businessId(business.getId())
                .businessName(business.getName())
                .businessType(business.getType())
                .ownerName(owner.getFullName())
                .ownerEmail(owner.getEmail())
                .phone(business.getPhone())
                .address(business.getAddress())
                .plan(business.getPlan())
                .subscriptionStatus(business.getSubscriptionStatus())
                .subscriptionEndDate(business.getSubscriptionEndDate())
                .totalClients(totalClients)
                .totalServices(totalServices)
                .totalAppointments(totalAppointments)
                .monthlyRevenue(monthlyRevenue)
                .message(message)
                .build();
    }
}
