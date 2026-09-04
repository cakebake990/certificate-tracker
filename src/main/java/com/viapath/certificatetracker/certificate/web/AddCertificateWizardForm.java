package com.viapath.certificatetracker.certificate.web;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public class AddCertificateWizardForm implements Serializable {
    private Long customerId;
    private String name;
    private String certificateTypeCode;
    private String issuer;
    private LocalDate expirationDate;
    private String serialNumber;
    private String fingerprint;
    private String subjectCommonName;
    private String notes;
    private Set<String> functionUsageCodes = new LinkedHashSet<>();
    private Set<Long> systemIds = new LinkedHashSet<>();
    private Set<String> environmentCodes = new LinkedHashSet<>();
    private String hostnameInput;
    private Set<String> hostnames = new LinkedHashSet<>();
    private Set<Long> siteIds = new LinkedHashSet<>();

    public void mergeHostnameInput() {
        if (hostnameInput != null) {
            for (var value : hostnameInput.split("[,\\r\\n]+")) if (!value.isBlank()) hostnames.add(value.trim());
        }
        hostnameInput = null;
    }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long value) { customerId = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getCertificateTypeCode() { return certificateTypeCode; }
    public void setCertificateTypeCode(String value) { certificateTypeCode = value; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String value) { issuer = value; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate value) { expirationDate = value; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String value) { serialNumber = value; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String value) { fingerprint = value; }
    public String getSubjectCommonName() { return subjectCommonName; }
    public void setSubjectCommonName(String value) { subjectCommonName = value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { notes = value; }
    public Set<String> getFunctionUsageCodes() { return functionUsageCodes; }
    public void setFunctionUsageCodes(Set<String> value) { functionUsageCodes = value == null ? new LinkedHashSet<>() : value; }
    public Set<Long> getSystemIds() { return systemIds; }
    public void setSystemIds(Set<Long> value) { systemIds = value == null ? new LinkedHashSet<>() : value; }
    public Set<String> getEnvironmentCodes() { return environmentCodes; }
    public void setEnvironmentCodes(Set<String> value) { environmentCodes = value == null ? new LinkedHashSet<>() : value; }
    public String getHostnameInput() { return hostnameInput; }
    public void setHostnameInput(String value) { hostnameInput = value; }
    public Set<String> getHostnames() { return hostnames; }
    public void setHostnames(Set<String> value) { hostnames = value == null ? new LinkedHashSet<>() : value; }
    public Set<Long> getSiteIds() { return siteIds; }
    public void setSiteIds(Set<Long> value) { siteIds = value == null ? new LinkedHashSet<>() : value; }
}
