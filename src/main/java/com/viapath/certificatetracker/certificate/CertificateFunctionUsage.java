package com.viapath.certificatetracker.certificate;
import java.time.Instant;
import jakarta.persistence.*;
@Entity @Table(name="certificate_function_usage", uniqueConstraints=@UniqueConstraint(name="uq_certificate_function_usage", columnNames={"certificate_id","function_usage_id"}))
public class CertificateFunctionUsage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="certificate_id") private Certificate certificate;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="function_usage_id") private FunctionUsage functionUsage;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    protected CertificateFunctionUsage() {}
    CertificateFunctionUsage(Certificate certificate, FunctionUsage value) { this.certificate=certificate; this.functionUsage=value; }
    @PrePersist void created(){ createdAt=Instant.now(); }
    public FunctionUsage getFunctionUsage(){ return functionUsage; }
}
