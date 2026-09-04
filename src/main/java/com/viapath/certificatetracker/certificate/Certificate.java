package com.viapath.certificatetracker.certificate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.viapath.certificatetracker.customer.Customer;
import com.viapath.certificatetracker.customer.Site;
import com.viapath.certificatetracker.shared.AuditedEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "certificate")
public class Certificate extends AuditedEntity {
    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "certificate_type_id", nullable = false)
    private CertificateType certificateType;

    @NotBlank @Size(max = 200)
    @Column(name = "certificate_name", nullable = false, length = 200)
    private String name;

    @Size(max = 500) private String issuer;
    @NotNull @Column(name = "expiration_date", nullable = false) private LocalDate expirationDate;
    @Size(max = 200) @Column(name = "serial_number", length = 200) private String serialNumber;
    @Size(max = 256) @Column(length = 256) private String fingerprint;
    @Size(max = 500) @Column(name = "subject_common_name", length = 500) private String subjectCommonName;
    @Column(columnDefinition = "nvarchar(max)") private String notes;
    @Column(nullable = false) private boolean active = true;

    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<@Valid CertificateHostname> hostnames = new LinkedHashSet<>();
    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CertificateFunctionUsage> functionUsages = new LinkedHashSet<>();
    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CertificateSystemIntegration> systems = new LinkedHashSet<>();
    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CertificateEnvironment> environments = new LinkedHashSet<>();
    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CertificateSite> sites = new LinkedHashSet<>();

    protected Certificate() {}
    public Certificate(Customer customer, CertificateType type, String name, LocalDate expirationDate) {
        this.customer = customer;
        this.certificateType = type;
        this.name = name == null ? null : name.trim();
        this.expirationDate = expirationDate;
    }

    public void setIdentityMetadata(String issuer, String serialNumber, String fingerprint, String subjectCommonName, String notes) {
        this.issuer = trim(issuer);
        this.serialNumber = trim(serialNumber);
        this.fingerprint = normalizeFingerprint(fingerprint);
        this.subjectCommonName = trim(subjectCommonName);
        this.notes = trim(notes);
    }
    public void addHostname(String hostname) { hostnames.add(new CertificateHostname(this, hostname)); }
    public void addFunctionUsage(FunctionUsage value) { functionUsages.add(new CertificateFunctionUsage(this, value)); }
    public void addSystem(SystemIntegration value) { systems.add(new CertificateSystemIntegration(this, value)); }
    public void addEnvironment(DeploymentEnvironment value) { environments.add(new CertificateEnvironment(this, value)); }
    public void addSite(Site value) {
        if (customer.getId() != null && !customer.getId().equals(value.getCustomer().getId())) {
            throw new IllegalArgumentException("Site must belong to the certificate customer");
        }
        sites.add(new CertificateSite(this, value));
    }
    private static String trim(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    static String normalizeFingerprint(String value) {
        var trimmed = trim(value);
        return trimmed == null ? null : trimmed.replace(":", "").replace(" ", "").toUpperCase();
    }
    public Customer getCustomer() { return customer; }
    public CertificateType getCertificateType() { return certificateType; }
    public String getName() { return name; }
    public String getIssuer() { return issuer; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public String getSerialNumber() { return serialNumber; }
    public String getFingerprint() { return fingerprint; }
    public String getSubjectCommonName() { return subjectCommonName; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public Set<CertificateHostname> getHostnames() { return Collections.unmodifiableSet(hostnames); }
    public Set<CertificateFunctionUsage> getFunctionUsages() { return Collections.unmodifiableSet(functionUsages); }
    public Set<CertificateSystemIntegration> getSystems() { return Collections.unmodifiableSet(systems); }
    public Set<CertificateEnvironment> getEnvironments() { return Collections.unmodifiableSet(environments); }
    public Set<CertificateSite> getSites() { return Collections.unmodifiableSet(sites); }
}
