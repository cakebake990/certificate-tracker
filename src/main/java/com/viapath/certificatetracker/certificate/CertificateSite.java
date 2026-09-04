package com.viapath.certificatetracker.certificate;
import java.time.Instant;
import com.viapath.certificatetracker.customer.Site;
import jakarta.persistence.*;
@Entity @Table(name="certificate_site", uniqueConstraints=@UniqueConstraint(name="uq_certificate_site", columnNames={"certificate_id","site_id"}))
public class CertificateSite {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="certificate_id") private Certificate certificate;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="site_id") private Site site;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    protected CertificateSite() {}
    CertificateSite(Certificate certificate, Site value){this.certificate=certificate;this.site=value;}
    @PrePersist void created(){createdAt=Instant.now();}
    public Site getSite(){return site;}
}
