package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.BusinessType;
import com.vaultify.vaultify_backend.model.Plan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBusinessRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @Email(message = "Owner email must be valid")
    @NotBlank(message = "Owner email is required")
    private String ownerEmail;

    @NotBlank(message = "Owner password is required")
    private String ownerPassword;

    private String phone;

    private String address;

    @NotNull(message = "Plan is required")
    private Plan plan;
}
