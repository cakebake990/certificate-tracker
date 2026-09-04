package com.viapath.certificatetracker.certificate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FunctionUsageRepository extends JpaRepository<FunctionUsage, Long> { List<FunctionUsage> findByCodeInAndActiveTrue(Collection<String> codes); }
