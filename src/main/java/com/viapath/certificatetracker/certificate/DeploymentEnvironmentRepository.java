package com.viapath.certificatetracker.certificate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeploymentEnvironmentRepository extends JpaRepository<DeploymentEnvironment, Long> { List<DeploymentEnvironment> findByCodeInAndActiveTrue(Collection<String> codes); }
