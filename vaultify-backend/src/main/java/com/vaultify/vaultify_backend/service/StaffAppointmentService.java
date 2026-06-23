package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.AppointmentResponse;
import com.vaultify.vaultify_backend.model.Appointment;
import com.vaultify.vaultify_backend.model.BusinessService;
import com.vaultify.vaultify_backend.model.Client;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.AppointmentRepository;
import com.vaultify.vaultify_backend.repository.ClientRepository;
import com.vaultify.vaultify_backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAppointmentService {

    private final CurrentUserService currentUserService;
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;

    public List<AppointmentResponse> getMyBusinessAppointments() {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        return appointmentRepository.findByBusinessIdAndActive(staff.getBusinessId(), true)
                .stream()
                .map(appointment -> mapToResponse(appointment, "Appointment loaded successfully"))
                .toList();
    }

    public List<AppointmentResponse> getTodayAppointments() {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        return appointmentRepository
                .findByBusinessIdAndActiveAndAppointmentDateTimeBetween(
                        staff.getBusinessId(),
                        true,
                        startOfDay,
                        startOfTomorrow
                )
                .stream()
                .map(appointment -> mapToResponse(appointment, "Today appointment loaded successfully"))
                .toList();
    }

    public AppointmentResponse getAppointmentById(String appointmentId) {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        Appointment appointment = appointmentRepository.findByIdAndBusinessId(appointmentId, staff.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.isActive()) {
            throw new RuntimeException("Appointment is inactive");
        }

        return mapToResponse(appointment, "Appointment loaded successfully");
    }

    private AppointmentResponse mapToResponse(Appointment appointment, String message) {
        Client client = clientRepository.findById(appointment.getClientId()).orElse(null);
        BusinessService service = serviceRepository.findById(appointment.getServiceId()).orElse(null);

        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .businessId(appointment.getBusinessId())
                .clientId(appointment.getClientId())
                .clientName(client != null ? client.getFullName() : null)
                .clientPhone(client != null ? client.getPhone() : null)
                .serviceId(appointment.getServiceId())
                .serviceName(service != null ? service.getName() : null)
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus())
                .price(appointment.getPrice())
                .durationMinutes(appointment.getDurationMinutes())
                .notes(appointment.getNotes())
                .active(appointment.isActive())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .message(message)
                .build();
    }
}
