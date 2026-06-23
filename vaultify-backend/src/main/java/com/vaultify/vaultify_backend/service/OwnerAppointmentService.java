package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.AppointmentResponse;
import com.vaultify.vaultify_backend.dto.CreateAppointmentRequest;
import com.vaultify.vaultify_backend.dto.UpdateAppointmentRequest;
import com.vaultify.vaultify_backend.model.Appointment;
import com.vaultify.vaultify_backend.model.AppointmentStatus;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.BusinessService;
import com.vaultify.vaultify_backend.model.Client;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.AppointmentRepository;
import com.vaultify.vaultify_backend.repository.ClientRepository;
import com.vaultify.vaultify_backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerAppointmentService {

    private final CurrentUserService currentUserService;
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final AuditLogService auditLogService;

    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        User currentUser = getCurrentOwner();

        Client client = clientRepository.findByIdAndBusinessId(request.getClientId(), currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Client not found for this business"));

        BusinessService service = serviceRepository.findByIdAndBusinessId(request.getServiceId(), currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Service not found for this business"));

        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = Appointment.builder()
                .businessId(currentUser.getBusinessId())
                .clientId(client.getId())
                .serviceId(service.getId())
                .appointmentDateTime(request.getAppointmentDateTime())
                .status(AppointmentStatus.CONFIRMED)
                .price(service.getPrice())
                .durationMinutes(service.getDurationMinutes())
                .notes(request.getNotes())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.CREATE_APPOINTMENT,
                "APPOINTMENT",
                savedAppointment.getId(),
                "Created appointment for client " + client.getFullName() + " with service " + service.getName()
        );

        return mapToResponse(savedAppointment, client, service, "Appointment created successfully");
    }

    public List<AppointmentResponse> getMyAppointments() {
        User currentUser = getCurrentOwner();

        return appointmentRepository.findByBusinessId(currentUser.getBusinessId())
                .stream()
                .map(appointment -> {
                    Client client = clientRepository.findById(appointment.getClientId()).orElse(null);
                    BusinessService service = serviceRepository.findById(appointment.getServiceId()).orElse(null);
                    return mapToResponse(appointment, client, service, "Appointment loaded successfully");
                })
                .toList();
    }

    public AppointmentResponse getAppointmentById(String appointmentId) {
        User currentUser = getCurrentOwner();

        Appointment appointment = appointmentRepository.findByIdAndBusinessId(appointmentId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Client client = clientRepository.findById(appointment.getClientId()).orElse(null);
        BusinessService service = serviceRepository.findById(appointment.getServiceId()).orElse(null);

        return mapToResponse(appointment, client, service, "Appointment loaded successfully");
    }

    public AppointmentResponse updateAppointment(String appointmentId, UpdateAppointmentRequest request) {
        User currentUser = getCurrentOwner();

        Appointment appointment = appointmentRepository.findByIdAndBusinessId(appointmentId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        AppointmentStatus previousStatus = appointment.getStatus();

        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());
        appointment.setActive(request.isActive());
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        Client client = clientRepository.findById(updatedAppointment.getClientId()).orElse(null);
        BusinessService service = serviceRepository.findById(updatedAppointment.getServiceId()).orElse(null);

        updateClientStatsIfCompleted(previousStatus, updatedAppointment, client);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.UPDATE_APPOINTMENT,
                "APPOINTMENT",
                updatedAppointment.getId(),
                "Updated appointment status to " + updatedAppointment.getStatus()
        );

        return mapToResponse(updatedAppointment, client, service, "Appointment updated successfully");
    }

    public AppointmentResponse deleteAppointment(String appointmentId) {
        User currentUser = getCurrentOwner();

        Appointment appointment = appointmentRepository.findByIdAndBusinessId(appointmentId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setActive(false);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        Client client = clientRepository.findById(cancelledAppointment.getClientId()).orElse(null);
        BusinessService service = serviceRepository.findById(cancelledAppointment.getServiceId()).orElse(null);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.CANCEL_APPOINTMENT,
                "APPOINTMENT",
                cancelledAppointment.getId(),
                "Cancelled appointment"
        );

        return mapToResponse(cancelledAppointment, client, service, "Appointment cancelled successfully");
    }

    private void updateClientStatsIfCompleted(
            AppointmentStatus previousStatus,
            Appointment appointment,
            Client client
    ) {
        if (client == null) {
            return;
        }

        boolean wasNotCompletedBefore = previousStatus != AppointmentStatus.COMPLETED;
        boolean isCompletedNow = appointment.getStatus() == AppointmentStatus.COMPLETED;

        if (wasNotCompletedBefore && isCompletedNow) {
            client.setTotalVisits(client.getTotalVisits() + 1);
            client.setTotalSpent(client.getTotalSpent() + appointment.getPrice());
            client.setLastVisitDate(appointment.getAppointmentDateTime());
            client.setUpdatedAt(LocalDateTime.now());

            clientRepository.save(client);
        }
    }

    private User getCurrentOwner() {
        return currentUserService.getCurrentOwnerWithActiveBusiness();
    }

    private AppointmentResponse mapToResponse(
            Appointment appointment,
            Client client,
            BusinessService service,
            String message
    ) {
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
