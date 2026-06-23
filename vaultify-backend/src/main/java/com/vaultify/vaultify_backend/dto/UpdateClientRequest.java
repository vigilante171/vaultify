package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateClientRequest {

    @NotBlank(message = "Client full name is required")
    private String fullName;

    @NotBlank(message = "Client phone is required")
    private String phone;

    private String email;

    private Gender gender;

    private String notes;

    private boolean active;
}
