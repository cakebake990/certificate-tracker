package com.viapath.certificatetracker.certificate;

public class CertificateNotFoundException extends RuntimeException {
    public CertificateNotFoundException(long id) { super("Certificate not found: " + id); }
}
