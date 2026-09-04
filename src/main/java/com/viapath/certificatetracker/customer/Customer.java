package com.viapath.certificatetracker.customer;

import com.viapath.certificatetracker.shared.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(name = "uq_customer_code", columnNames = "customer_code"))
public class Customer extends AuditedEntity {
    @NotBlank @Size(max = 50)
    @Column(name = "customer_code", nullable = false, length = 50)
    private String code;

    @NotBlank @Size(max = 200)
    @Column(name = "customer_name", nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    protected Customer() {}
    public Customer(String code, String name) {
        this.code = normalizeCode(code);
        this.name = name == null ? null : name.trim();
    }
    public static String normalizeCode(String value) { return value == null ? null : value.trim().toUpperCase(); }
    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
    public void rename(String name) { this.name = name == null ? null : name.trim(); }
}
