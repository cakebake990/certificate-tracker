package com.viapath.certificatetracker.certificate;
import java.time.Instant;
import jakarta.persistence.*;
@Entity @Table(name="certificate_environment", uniqueConstraints=@UniqueConstraint(name="uq_certificate_environment", columnNames={"certificate_id","environment_id"}))
public class CertificateEnvironment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="certificate_id") private Certificate certificate;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="environment_id") private DeploymentEnvironment environment;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    protected CertificateEnvironment() {}
    CertificateEnvironment(Certificate certificate, DeploymentEnvironment value){this.certificate=certificate;this.environment=value;}
    @PrePersist void created(){createdAt=Instant.now();}
    public DeploymentEnvironment getEnvironment(){return environment;}
}
