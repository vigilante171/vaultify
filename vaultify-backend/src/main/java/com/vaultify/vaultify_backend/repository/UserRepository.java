package com.vaultify.vaultify_backend.repository;

import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByBusinessId(String businessId);

    List<User> findByBusinessIdAndRole(String businessId, Role role);

    Optional<User> findByIdAndBusinessId(String id, String businessId);

    List<User> findByRole(Role role);

    long countByRole(Role role);
}
