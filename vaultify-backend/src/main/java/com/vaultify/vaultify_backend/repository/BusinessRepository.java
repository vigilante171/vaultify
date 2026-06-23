package com.vaultify.vaultify_backend.repository;

import com.vaultify.vaultify_backend.model.Business;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BusinessRepository extends MongoRepository<Business, String> {

    List<Business> findBySubscriptionStatus(SubscriptionStatus status);

    List<Business> findByActive(boolean active);

    long countByActive(boolean active);

    long countBySubscriptionStatus(SubscriptionStatus status);

    List<Business> findByNameContainingIgnoreCase(String name);

    List<Business> findByActiveAndSubscriptionStatus(boolean active, SubscriptionStatus status);
}
