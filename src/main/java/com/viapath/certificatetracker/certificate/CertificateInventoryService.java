package com.viapath.certificatetracker.certificate;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

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
    private final Clock clock;

    public CertificateInventoryService(CustomerRepository customers, SiteRepository sites,
            CertificateRepository certificates, CertificateTypeRepository types,
            FunctionUsageRepository usages, SystemIntegrationRepository systems,
            DeploymentEnvironmentRepository environments, Validator validator, Clock clock) {
        this.customers = customers; this.sites = sites; this.certificates = certificates; this.types = types;
        this.usages = usages; this.systems = systems; this.environments = environments; this.validator = validator; this.clock = clock;
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
            var site = sites.findById(siteId).filter(Site::isActive)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown active site: " + siteId));
            certificate.addSite(site);
        }
        for (var hostname : empty(command.hostnames())) certificate.addHostname(hostname);
        validate(certificate);
        return certificates.save(certificate);
    }

    @Transactional(readOnly = true)
    public Certificate getCertificateDetail(long id) {
        return certificates.findDetailById(id).orElseThrow(() -> new CertificateNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<CertificateSummary> searchCertificates(String query, Long customerId, ExpirationHealth health, Long systemId) {
        Specification<Certificate> spec = (root, criteria, builder) -> builder.isTrue(root.get("active"));
        if (query != null && !query.isBlank()) {
            var pattern = "%" + query.trim().toLowerCase() + "%";
            spec = spec.and((root, criteria, builder) -> builder.like(builder.lower(root.get("name")), pattern));
        }
        if (customerId != null) spec = spec.and((root, criteria, builder) -> builder.equal(root.get("customer").get("id"), customerId));
        if (systemId != null) spec = spec.and((root, criteria, builder) -> {
            criteria.distinct(true);
            return builder.equal(root.join("systems").get("systemIntegration").get("id"), systemId);
        });
        if (health != null) spec = spec.and(healthSpecification(health, LocalDate.now(clock.withZone(ExpirationHealth.BUSINESS_ZONE))));
        return certificates.findAll(spec, Sort.by("expirationDate").ascending().and(Sort.by("name")))
                .stream().map(this::summary).toList();
    }

    @Transactional(readOnly = true)
    public CertificateDetail findCertificateDetail(long id) {
        var value = getCertificateDetail(id);
        return new CertificateDetail(value.getId(), value.getName(), value.getCustomer().getName(),
                value.getCertificateType().getDisplayName(), value.getIssuer(), value.getExpirationDate(),
                ExpirationHealth.calculate(value.getExpirationDate(), clock), value.getSerialNumber(), value.getFingerprint(),
                value.getSubjectCommonName(), value.getNotes(), value.getCreatedAt(), value.getUpdatedAt(),
                sortedLabels(value.getFunctionUsages().stream().map(link -> link.getFunctionUsage().getDisplayName())),
                sortedLabels(value.getSystems().stream().map(link -> link.getSystemIntegration().getDisplayName())),
                sortedLabels(value.getEnvironments().stream().map(link -> link.getEnvironment().getDisplayName())),
                value.getHostnames().stream().map(CertificateHostname::getHostname).sorted().toList(),
                value.getSites().stream().map(link -> link.getSite().getDisplayName()).sorted().toList());
    }

    @Transactional(readOnly = true)
    public WizardOptions wizardOptions(Long customerId) {
        var customerOptions = customers.findByActiveTrueOrderByNameAsc().stream().map(c -> new Option(c.getId().toString(), c.getName())).toList();
        var typeOptions = types.findAll().stream().filter(CertificateType::isActive).map(t -> new Option(t.getCode(), t.getDisplayName())).toList();
        var usageOptions = usages.findAll().stream().filter(FunctionUsage::isActive).map(u -> new Option(u.getCode(), u.getDisplayName())).toList();
        var environmentOptions = environments.findAll().stream().filter(DeploymentEnvironment::isActive).map(e -> new Option(e.getCode(), e.getDisplayName())).toList();
        var systemOptions = systems.findByActiveTrueOrderByDisplayNameAsc().stream().map(s -> new Option(s.getId().toString(), s.getDisplayName())).toList();
        var siteOptions = customerId == null ? List.<Option>of() : sites.findByCustomerIdAndActiveTrueOrderByDisplayNameAsc(customerId).stream().map(s -> new Option(s.getId().toString(), s.getDisplayName())).toList();
        return new WizardOptions(customerOptions, typeOptions, usageOptions, systemOptions, environmentOptions, siteOptions);
    }

    @Transactional(readOnly = true)
    public String customerName(long id) { return customer(id).getName(); }

    private CertificateSummary summary(Certificate value) {
        return new CertificateSummary(value.getId(), value.getCustomer().getName(), value.getName(),
                value.getCertificateType().getDisplayName(), value.getExpirationDate(),
                ExpirationHealth.calculate(value.getExpirationDate(), clock),
                sortedLabels(value.getFunctionUsages().stream().map(link -> link.getFunctionUsage().getDisplayName())),
                sortedLabels(value.getSystems().stream().map(link -> link.getSystemIntegration().getDisplayName())), value.getSites().size());
    }
    private static List<String> sortedLabels(java.util.stream.Stream<String> values) {
        return values.sorted().toList();
    }
    private static Specification<Certificate> healthSpecification(ExpirationHealth health, LocalDate today) {
        return (root, query, b) -> switch (health) {
            case EXPIRED -> b.lessThan(root.get("expirationDate"), today);
            case EXPIRING_30 -> b.between(root.get("expirationDate"), today, today.plusDays(30));
            case EXPIRING_60 -> b.between(root.get("expirationDate"), today.plusDays(31), today.plusDays(60));
            case EXPIRING_90 -> b.between(root.get("expirationDate"), today.plusDays(61), today.plusDays(90));
            case ACTIVE -> b.greaterThan(root.get("expirationDate"), today.plusDays(90));
        };
    }

    private Customer customer(long id) { return customers.findById(id).filter(Customer::isActive).orElseThrow(() -> new IllegalArgumentException("Active customer not found: " + id)); }
    private void validate(Object value) { var violations = validator.validate(value); if (!violations.isEmpty()) throw new ConstraintViolationException(violations); }
    private <T, ID> T saveValidated(org.springframework.data.repository.CrudRepository<T, ID> repository, T value) { validate(value); return repository.save(value); }
    private static <T> Set<T> empty(Set<T> values) { return values == null ? Set.of() : values; }
    private static void requireAll(String label, int expected, int found) { if (expected != found) throw new IllegalArgumentException("One or more " + label + " values are missing or inactive"); }

    public record CreateCertificate(long customerId, String name, String certificateTypeCode,
            LocalDate expirationDate, String issuer, String serialNumber, String fingerprint,
            String subjectCommonName, String notes, Set<String> functionUsageCodes,
            Set<Long> systemIds, Set<String> environmentCodes, Set<String> hostnames, Set<Long> siteIds) {}
    public record Option(String value, String label) {}
    public record WizardOptions(List<Option> customers, List<Option> certificateTypes, List<Option> functionUsages,
            List<Option> systems, List<Option> environments, List<Option> sites) {}
    public record CertificateSummary(Long id, String customer, String name, String type, LocalDate expirationDate,
            ExpirationHealth health, List<String> functions, List<String> systems, int siteCount) {}
    public record CertificateDetail(Long id, String name, String customer, String type, String issuer,
            LocalDate expirationDate, ExpirationHealth health, String serialNumber, String fingerprint,
            String subjectCommonName, String notes, java.time.Instant createdAt, java.time.Instant updatedAt,
            List<String> functions, List<String> systems, List<String> environments, List<String> hostnames, List<String> sites) {}
}
