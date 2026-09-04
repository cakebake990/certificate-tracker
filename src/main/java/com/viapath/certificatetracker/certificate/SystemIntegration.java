package com.viapath.certificatetracker.certificate;

import com.viapath.certificatetracker.shared.AuditedEntity;
import com.viapath.certificatetracker.customer.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity @Table(name = "system_integration")
public class SystemIntegration extends AuditedEntity {
    @NotBlank @Size(max = 100)
    @Column(name = "system_code", nullable = false, unique = true, length = 100)
    private String code;
    @NotBlank @Size(max = 200)
    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;
    @Column(nullable = false)
    private boolean active = true;
    protected SystemIntegration() {}
    public SystemIntegration(String code, String displayName) {
        this.code = Customer.normalizeCode(code);
        this.displayName = displayName == null ? null : displayName.trim();
    }
    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
}
