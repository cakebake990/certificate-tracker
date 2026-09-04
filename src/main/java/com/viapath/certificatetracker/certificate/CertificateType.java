package com.viapath.certificatetracker.certificate;

import com.viapath.certificatetracker.shared.ReferenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "certificate_type")
public class CertificateType extends ReferenceEntity {
    @Column(name = "type_code", nullable = false, unique = true, length = 50)
    private String code;
    protected CertificateType() {}
    public CertificateType(String code, String displayName) { super(displayName); this.code = code; }
    public String getCode() { return code; }
}
