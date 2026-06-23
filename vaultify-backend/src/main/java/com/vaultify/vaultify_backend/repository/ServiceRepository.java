package com.vaultify.vaultify_backend.repository;

import com.vaultify.vaultify_backend.model.BusinessService;
import com.vaultify.vaultify_backend.model.ServiceCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends MongoRepository<BusinessService, String> {

    List<BusinessService> findByBusinessId(String businessId);

    List<BusinessService> findByBusinessIdAndActive(String businessId, boolean active);

    Optional<BusinessService> findByIdAndBusinessId(String id, String businessId);

    boolean existsByBusinessIdAndNameIgnoreCase(String businessId, String name);

    long countByBusinessId(String businessId);

    long countByBusinessIdAndActive(String businessId, boolean active);

    List<BusinessService> findByBusinessIdAndNameContainingIgnoreCase(String businessId, String name);

    List<BusinessService> findByBusinessIdAndCategory(String businessId, ServiceCategory category);
}
