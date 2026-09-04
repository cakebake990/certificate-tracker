package com.viapath.certificatetracker.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Set;

import com.viapath.certificatetracker.customer.CustomerRepository;
import com.viapath.certificatetracker.customer.SiteRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CertificateInventoryServiceTests {
    @Autowired CertificateInventoryService service;
    @Autowired CustomerRepository customers;
    @Autowired SiteRepository sites;
    @Autowired CertificateRepository certificates;
    @Autowired CertificateTypeRepository types;
    @Autowired FunctionUsageRepository usages;
    @Autowired DeploymentEnvironmentRepository environments;
    @Autowired EntityManager entityManager;

    @BeforeEach
    void seedReferences() {
        types.save(new CertificateType("TLS_SSL", "TLS/SSL"));
        usages.save(new FunctionUsage("APPLICATION", "Application"));
        usages.save(new FunctionUsage("API", "API"));
        environments.save(new DeploymentEnvironment("PRODUCTION", "Production"));
    }

    @Test
    void createsCustomerAndCustomerOwnedSite() {
        var customer = service.createCustomer(" acme ", "Acme Agency");
        var site = service.createSite(customer.getId(), " north ", "North Site");

        assertThat(customer.getCode()).isEqualTo("ACME");
        assertThat(site.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(sites.findByCustomerIdAndActiveTrueOrderByDisplayNameAsc(customer.getId())).containsExactly(site);
        assertThat(customers.findByActiveTrueOrderByNameAsc()).contains(customer);
    }

    @Test
    void rejectsMissingRequiredCustomerDataAndDuplicateCodes() {
        service.createCustomer("ACME", "Acme");
        assertThatThrownBy(() -> service.createCustomer("acme", "Duplicate")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.createCustomer("X", " ")).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void createsAndRetrievesCertificateWithAllOptionalAssociations() {
        var customer = service.createCustomer("ACME", "Acme");
        var site = service.createSite(customer.getId(), "NORTH", "North");
        var system = service.createSystem("oms", "OMS");
        var command = new CertificateInventoryService.CreateCertificate(customer.getId(), "Gateway TLS", "TLS_SSL",
                LocalDate.of(2027, 1, 1), "Issuer", "01 AB", "AA:BB:CC", "gateway.example.org", "Notes",
                Set.of("APPLICATION", "API"), Set.of(system.getId()), Set.of("PRODUCTION"),
                Set.of("gateway.example.org", "*.service.example.org"), Set.of(site.getId()));

        var created = service.createCertificate(command);
        entityManager.flush();
        entityManager.clear();
        var detail = service.getCertificateDetail(created.getId());

        assertThat(detail.getFingerprint()).isEqualTo("AABBCC");
        assertThat(detail.getFunctionUsages()).hasSize(2);
        assertThat(detail.getSystems()).hasSize(1);
        assertThat(detail.getEnvironments()).hasSize(1);
        assertThat(detail.getSites()).hasSize(1);
        assertThat(detail.getHostnames()).extracting(CertificateHostname::getHostname)
                .containsExactlyInAnyOrder("gateway.example.org", "*.service.example.org");
        assertThat(certificates.findByCustomerIdOrderByExpirationDateAsc(customer.getId())).hasSize(1);
        assertThat(certificates.findByNameContainingIgnoreCaseOrderByNameAsc("gateway")).hasSize(1);
        assertThat(certificates.findDistinctBySystemsSystemIntegrationId(system.getId())).hasSize(1);
        assertThat(certificates.findDistinctBySitesSiteId(site.getId())).hasSize(1);
    }

    @Test
    void requiresFunctionUsageButAllowsOtherConsumerRelationshipsToBeEmpty() {
        var customer = service.createCustomer("ACME", "Acme");
        var command = new CertificateInventoryService.CreateCertificate(customer.getId(), "Certificate", "TLS_SSL",
                LocalDate.now().plusDays(100), null, null, null, null, null,
                Set.of(), Set.of(), Set.of(), Set.of(), Set.of());
        assertThatThrownBy(() -> service.createCertificate(command))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Function / Usage");
    }

    @Test
    void rejectsInvalidHostnameAndCrossCustomerSite() {
        var customer = service.createCustomer("ONE", "One");
        var other = service.createCustomer("TWO", "Two");
        var otherSite = service.createSite(other.getId(), "SITE", "Other site");

        assertThatThrownBy(() -> service.createCertificate(command(customer.getId(), Set.of("bad host name"), Set.of())))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Hostname");
        assertThatThrownBy(() -> service.createCertificate(command(customer.getId(), Set.of(), Set.of(otherSite.getId()))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Site must belong");
    }

    @Test
    void mutableCoreRecordsHaveOptimisticVersion() {
        var customer = service.createCustomer("ACME", "Acme");
        entityManager.flush();
        assertThat(customer.getVersion()).isZero();
        customer.rename("Acme Agency");
        entityManager.flush();
        assertThat(customer.getVersion()).isEqualTo(1);
    }

    private CertificateInventoryService.CreateCertificate command(long customerId, Set<String> hostnames, Set<Long> siteIds) {
        return new CertificateInventoryService.CreateCertificate(customerId, "Certificate", "TLS_SSL", LocalDate.now().plusDays(100),
                null, null, null, null, null, Set.of("APPLICATION"), Set.of(), Set.of(), hostnames, siteIds);
    }
}
