package com.viapath.certificatetracker.customer;

import com.viapath.certificatetracker.shared.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "site", uniqueConstraints = @UniqueConstraint(name = "uq_site_customer_code", columnNames = {"customer_id", "site_code"}))
public class Site extends AuditedEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank @Size(max = 50)
    @Column(name = "site_code", nullable = false, length = 50)
    private String code;

    @NotBlank @Size(max = 200)
    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Column(nullable = false)
    private boolean active = true;

    protected Site() {}
    public Site(Customer customer, String code, String displayName) {
        this.customer = customer;
        this.code = Customer.normalizeCode(code);
        this.displayName = displayName == null ? null : displayName.trim();
    }
    public Customer getCustomer() { return customer; }
    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public boolean isActive() { return active; }
}
