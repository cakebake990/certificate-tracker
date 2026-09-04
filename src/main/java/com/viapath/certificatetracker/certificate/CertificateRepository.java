package com.viapath.certificatetracker.certificate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CertificateRepository extends JpaRepository<Certificate, Long>, JpaSpecificationExecutor<Certificate> {
    List<Certificate> findByCustomerIdOrderByExpirationDateAsc(Long customerId);
    List<Certificate> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    List<Certificate> findByActiveTrueAndExpirationDateLessThanEqualOrderByExpirationDateAsc(LocalDate date);
    List<Certificate> findDistinctBySystemsSystemIntegrationId(Long systemId);
    List<Certificate> findDistinctBySitesSiteId(Long siteId);

    @EntityGraph(attributePaths = {"customer", "certificateType", "hostnames", "functionUsages.functionUsage",
            "systems.systemIntegration", "environments.environment", "sites.site"})
    Optional<Certificate> findDetailById(Long id);
}
