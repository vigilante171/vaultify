package com.vaultify.vaultify_backend.repository;

import com.vaultify.vaultify_backend.model.Appointment;
import com.vaultify.vaultify_backend.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByBusinessId(String businessId);

    List<Appointment> findByBusinessIdAndActive(String businessId, boolean active);

    Optional<Appointment> findByIdAndBusinessId(String id, String businessId);

    List<Appointment> findByBusinessIdAndStatus(String businessId, AppointmentStatus status);

    List<Appointment> findByBusinessIdAndAppointmentDateTimeBetween(
            String businessId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    List<Appointment> findByBusinessIdAndActiveAndAppointmentDateTimeBetween(
            String businessId,
            boolean active,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    List<Appointment> findByBusinessIdAndStatusAndAppointmentDateTimeBetween(
            String businessId,
            AppointmentStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    long countByBusinessId(String businessId);

    long countByBusinessIdAndActive(String businessId, boolean active);
}
