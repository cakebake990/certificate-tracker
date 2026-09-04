package com.viapath.certificatetracker.certificate;

import java.time.LocalDate;
import java.util.Set;

import com.viapath.certificatetracker.customer.Customer;
import com.viapath.certificatetracker.customer.CustomerRepository;
import com.viapath.certificatetracker.customer.Site;
import com.viapath.certificatetracker.customer.SiteRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({"dev", "qa", "test"})
public class CertificateInventoryService {
    private final CustomerRepository customers;
    private final SiteRepository sites;
    private final CertificateRepository certificates;
    private final CertificateTypeRepository types;
    private final FunctionUsageRepository usages;
    private final SystemIntegrationRepository systems;
    private final DeploymentEnvironmentRepository environments;
    private final Validator validator;

    public CertificateInventoryService(CustomerRepository customers, SiteRepository sites,
            CertificateRepository certificates, CertificateTypeRepository types,
            FunctionUsageRepository usages, SystemIntegrationRepository systems,
            DeploymentEnvironmentRepository environments, Validator validator) {
        this.customers = customers; this.sites = sites; this.certificates = certificates; this.types = types;
        this.usages = usages; this.systems = systems; this.environments = environments; this.validator = validator;
    }

    @Transactional
    public Customer createCustomer(String code, String name) {
        var normalized = Customer.normalizeCode(code);
        if (normalized != null && customers.existsByCodeIgnoreCase(normalized)) throw new IllegalArgumentException("Customer code already exists");
        return saveValidated(customers, new Customer(normalized, name));
    }

    @Transactional
    public Site createSite(long customerId, String code, String displayName) {
        var customer = customer(customerId);
        var normalized = Customer.normalizeCode(code);
        if (normalized != null && sites.existsByCustomerIdAndCodeIgnoreCase(customerId, normalized)) throw new IllegalArgumentException("Site code already exists for customer");
        return saveValidated(sites, new Site(customer, normalized, displayName));
    }

    @Transactional
    public SystemIntegration createSystem(String code, String displayName) {
        var normalized = Customer.normalizeCode(code);
        if (normalized != null && systems.existsByCodeIgnoreCase(normalized)) throw new IllegalArgumentException("System code already exists");
        return saveValidated(systems, new SystemIntegration(normalized, displayName));
    }

    @Transactional
    public Certificate createCertificate(CreateCertificate command) {
        if (command.functionUsageCodes() == null || command.functionUsageCodes().isEmpty()) throw new IllegalArgumentException("At least one Function / Usage is required");
        var certificate = new Certificate(customer(command.customerId()),
                types.findByCodeAndActiveTrue(command.certificateTypeCode()).orElseThrow(() -> new IllegalArgumentException("Unknown active certificate type")),
                command.name(), command.expirationDate());
        certificate.setIdentityMetadata(command.issuer(), command.serialNumber(), command.fingerprint(), command.subjectCommonName(), command.notes());

        var usageValues = usages.findByCodeInAndActiveTrue(command.functionUsageCodes());
        requireAll("Function / Usage", command.functionUsageCodes().size(), usageValues.size());
        usageValues.forEach(certificate::addFunctionUsage);

        var systemIds = empty(command.systemIds());
        var systemValues = systems.findByIdInAndActiveTrue(systemIds);
        requireAll("System / Integration", systemIds.size(), systemValues.size());
        systemValues.forEach(certificate::addSystem);

        var environmentCodes = empty(command.environmentCodes());
        var environmentValues = environments.findByCodeInAndActiveTrue(environmentCodes);
        requireAll("Environment", environmentCodes.size(), environmentValues.size());
        environmentValues.forEach(certificate::addEnvironment);

        for (var siteId : empty(command.siteIds())) {
            certificate.addSite(sites.findById(siteId).orElseThrow(() -> new IllegalArgumentException("Unknown site: " + siteId)));
        }
        for (var hostname : empty(command.hostnames())) certificate.addHostname(hostname);
        validate(certificate);
        return certificates.save(certificate);
    }

    @Transactional(readOnly = true)
    public Certificate getCertificateDetail(long id) {
        return certificates.findDetailById(id).orElseThrow(() -> new IllegalArgumentException("Certificate not found: " + id));
    }

    private Customer customer(long id) { return customers.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id)); }
    private void validate(Object value) { var violations = validator.validate(value); if (!violations.isEmpty()) throw new ConstraintViolationException(violations); }
    private <T, ID> T saveValidated(org.springframework.data.repository.CrudRepository<T, ID> repository, T value) { validate(value); return repository.save(value); }
    private static <T> Set<T> empty(Set<T> values) { return values == null ? Set.of() : values; }
    private static void requireAll(String label, int expected, int found) { if (expected != found) throw new IllegalArgumentException("One or more " + label + " values are missing or inactive"); }

    public record CreateCertificate(long customerId, String name, String certificateTypeCode,
            LocalDate expirationDate, String issuer, String serialNumber, String fingerprint,
            String subjectCommonName, String notes, Set<String> functionUsageCodes,
            Set<Long> systemIds, Set<String> environmentCodes, Set<String> hostnames, Set<Long> siteIds) {}
}
