package com.vaultify.vaultify_backend.config;

import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@vaultify.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User superAdmin = User.builder()
                    .businessId(null)
                    .fullName("Vaultify Super Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@12345"))
                    .role(Role.SUPER_ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(superAdmin);

            System.out.println("========================================");
            System.out.println("Vaultify SUPER_ADMIN created");
            System.out.println("Email: admin@vaultify.com");
            System.out.println("Password: Admin@12345");
            System.out.println("========================================");
        }
    }
}
