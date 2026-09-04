package com.viapath.certificatetracker.customer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByCodeIgnoreCase(String code);
    List<Customer> findByActiveTrueOrderByNameAsc();
}
