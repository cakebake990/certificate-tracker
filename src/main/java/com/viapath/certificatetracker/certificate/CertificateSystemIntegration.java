package com.viapath.certificatetracker.certificate;
import java.time.Instant;
import jakarta.persistence.*;
@Entity @Table(name="certificate_system_integration", uniqueConstraints=@UniqueConstraint(name="uq_certificate_system", columnNames={"certificate_id","system_integration_id"}))
public class CertificateSystemIntegration {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="certificate_id") private Certificate certificate;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="system_integration_id") private SystemIntegration systemIntegration;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    protected CertificateSystemIntegration() {}
    CertificateSystemIntegration(Certificate certificate, SystemIntegration value){this.certificate=certificate;this.systemIntegration=value;}
    @PrePersist void created(){createdAt=Instant.now();}
    public SystemIntegration getSystemIntegration(){return systemIntegration;}
}
