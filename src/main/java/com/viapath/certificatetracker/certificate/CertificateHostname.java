package com.viapath.certificatetracker.certificate;

import java.time.Instant;
import java.util.regex.Pattern;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "certificate_hostname", uniqueConstraints = @UniqueConstraint(name = "uq_certificate_hostname", columnNames = {"certificate_id", "hostname"}))
public class CertificateHostname {
    private static final Pattern HOST_PATTERN = Pattern.compile("^(?=.{1,253}$)(\\*\\.)?[A-Za-z0-9](?:[A-Za-z0-9.-]*[A-Za-z0-9])?$");
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "certificate_id", nullable = false) private Certificate certificate;
    @NotBlank @Size(max = 253) @Column(nullable = false, length = 253) private String hostname;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected CertificateHostname() {}
    CertificateHostname(Certificate certificate, String hostname) {
        var normalized = hostname == null ? null : hostname.trim().toLowerCase();
        if (normalized == null || !HOST_PATTERN.matcher(normalized).matches() || normalized.contains("..")) {
            throw new IllegalArgumentException("Hostname must be a valid DNS name, wildcard DNS name, or simple host label");
        }
        this.certificate = certificate;
        this.hostname = normalized;
    }
    @PrePersist void setCreatedAt() { createdAt = Instant.now(); }
    public Long getId() { return id; }
    public String getHostname() { return hostname; }
}
