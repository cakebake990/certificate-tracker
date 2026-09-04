package com.viapath.certificatetracker.customer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
    boolean existsByCustomerIdAndCodeIgnoreCase(Long customerId, String code);
    List<Site> findByCustomerIdAndActiveTrueOrderByDisplayNameAsc(Long customerId);
}
