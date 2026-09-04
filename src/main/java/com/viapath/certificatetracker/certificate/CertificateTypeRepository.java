package com.viapath.certificatetracker.certificate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CertificateTypeRepository extends JpaRepository<CertificateType, Long> { Optional<CertificateType> findByCodeAndActiveTrue(String code); }
