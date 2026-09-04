package com.viapath.certificatetracker.certificate.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Set;

import com.viapath.certificatetracker.certificate.CertificateInventoryService;
import com.viapath.certificatetracker.certificate.CertificateRepository;
import com.viapath.certificatetracker.certificate.CertificateType;
import com.viapath.certificatetracker.certificate.CertificateTypeRepository;
import com.viapath.certificatetracker.certificate.FunctionUsage;
import com.viapath.certificatetracker.certificate.FunctionUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CertificateWebControllerTests {
    @Autowired MockMvc mvc;
    @Autowired CertificateInventoryService inventory;
    @Autowired CertificateRepository certificates;
    @Autowired CertificateTypeRepository types;
    @Autowired FunctionUsageRepository usages;

    private long customerId;
    private long certificateId;

    @BeforeEach
    void seed() {
        types.save(new CertificateType("TLS_SSL", "TLS/SSL"));
        usages.save(new FunctionUsage("APPLICATION", "Application"));
        customerId = inventory.createCustomer("ACME", "Acme Agency").getId();
        certificateId = inventory.createCertificate(new CertificateInventoryService.CreateCertificate(customerId,
                "Gateway TLS", "TLS_SSL", LocalDate.now().plusDays(20), "Acme CA", null, null,
                "gateway.example.org", null, Set.of("APPLICATION"), Set.of(), Set.of(), Set.of(), Set.of())).getId();
    }

    @Test
    void inventoryLoadsAndAppliesSearchFilters() throws Exception {
        mvc.perform(get("/certificates").param("q", "Gateway").param("customer", Long.toString(customerId)))
                .andExpect(status().isOk()).andExpect(view().name("certificates/list"))
                .andExpect(content().string(containsString("Gateway TLS")))
                .andExpect(content().string(containsString("30 Days")));
        mvc.perform(get("/certificates").param("q", "not-present"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("No certificates match")));
    }

    @Test
    void detailLoadsAndUnknownCertificateReturnsOperational404() throws Exception {
        mvc.perform(get("/certificates/{id}", certificateId)).andExpect(status().isOk())
                .andExpect(content().string(containsString("gateway.example.org")))
                .andExpect(content().string(containsString("Acme CA")));
        mvc.perform(get("/certificates/{id}", 999999)).andExpect(status().isNotFound())
                .andExpect(view().name("error/operational"));
    }

    @Test
    void wizardValidatesCurrentStepAndPreservesStateWhenNavigatingBack() throws Exception {
        var session = new MockHttpSession();
        mvc.perform(get("/certificates/new/customer").session(session)).andExpect(status().isOk())
                .andExpect(content().string(containsString("Step 1 of 5")));
        mvc.perform(post("/certificates/new/customer").with(csrf()).session(session))
                .andExpect(status().isOk()).andExpect(view().name("certificates/add/customer"))
                .andExpect(content().string(containsString("Select a customer")));
        mvc.perform(post("/certificates/new/customer").with(csrf()).session(session)
                        .param("customerId", Long.toString(customerId)))
                .andExpect(redirectedUrl("/certificates/new/details"));
        mvc.perform(post("/certificates/new/details").with(csrf()).session(session)
                        .param("name", "New Edge Certificate").param("certificateTypeCode", "TLS_SSL")
                        .param("expirationDate", LocalDate.now().plusYears(1).toString()))
                .andExpect(redirectedUrl("/certificates/new/usage"));
        mvc.perform(get("/certificates/new/details").session(session)).andExpect(status().isOk())
                .andExpect(content().string(containsString("New Edge Certificate")));
    }

    @Test
    void completionCreatesOnceAndCancelClearsWizard() throws Exception {
        long before = certificates.count();
        var form = new AddCertificateWizardForm();
        form.setCustomerId(customerId);
        form.setName("New Edge Certificate");
        form.setCertificateTypeCode("TLS_SSL");
        form.setExpirationDate(LocalDate.now().plusYears(1));
        form.setFunctionUsageCodes(Set.of("APPLICATION"));
        var session = new MockHttpSession();
        session.setAttribute("certificateWizard", form);

        var completed = mvc.perform(post("/certificates/new/complete").with(csrf()).session(session))
                .andExpect(status().is3xxRedirection()).andReturn();
        var location = completed.getResponse().getRedirectedUrl();
        org.assertj.core.api.Assertions.assertThat(location).startsWith("/certificates/");
        org.assertj.core.api.Assertions.assertThat(certificates.count()).isEqualTo(before + 1);
        mvc.perform(get(location)).andExpect(status().isOk());
        mvc.perform(get(location)).andExpect(status().isOk());
        org.assertj.core.api.Assertions.assertThat(certificates.count()).isEqualTo(before + 1);

        var cancelSession = new MockHttpSession();
        cancelSession.setAttribute("certificateWizard", form);
        mvc.perform(post("/certificates/new/cancel").with(csrf()).session(cancelSession))
                .andExpect(redirectedUrl("/certificates"));
        mvc.perform(get("/certificates/new/details").session(cancelSession))
                .andExpect(redirectedUrl("/certificates/new/customer"));
    }
}
