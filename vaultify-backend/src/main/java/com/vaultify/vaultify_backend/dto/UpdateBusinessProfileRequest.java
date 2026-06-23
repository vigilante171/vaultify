package com.vaultify.vaultify_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateBusinessProfileRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String phone;

    private String address;

    private String logoUrl;
}
