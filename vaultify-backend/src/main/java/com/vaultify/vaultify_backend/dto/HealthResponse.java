package com.vaultify.vaultify_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String status;

    private String application;

    private String version;

    private String message;

    private List<String> modules;

    private LocalDateTime timestamp;
}
