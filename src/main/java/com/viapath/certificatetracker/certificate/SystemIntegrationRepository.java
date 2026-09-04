package com.viapath.certificatetracker.certificate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SystemIntegrationRepository extends JpaRepository<SystemIntegration, Long> {
    boolean existsByCodeIgnoreCase(String code);
    List<SystemIntegration> findByIdInAndActiveTrue(Collection<Long> ids);
    List<SystemIntegration> findByActiveTrueOrderByDisplayNameAsc();
}
