package com.vaultify.vaultify_backend.repository;

import com.vaultify.vaultify_backend.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, String> {

    List<Client> findByBusinessId(String businessId);

    List<Client> findByBusinessIdAndActive(String businessId, boolean active);

    Optional<Client> findByIdAndBusinessId(String id, String businessId);

    boolean existsByBusinessIdAndPhone(String businessId, String phone);

    long countByBusinessId(String businessId);

    long countByBusinessIdAndActive(String businessId, boolean active);

    List<Client> findByBusinessIdAndFullNameContainingIgnoreCase(String businessId, String fullName);

    List<Client> findByBusinessIdAndPhoneContaining(String businessId, String phone);

    List<Client> findByBusinessIdAndEmailContainingIgnoreCase(String businessId, String email);
}
