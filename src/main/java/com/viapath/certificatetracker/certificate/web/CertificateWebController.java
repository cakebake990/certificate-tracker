package com.viapath.certificatetracker.certificate.web;

import java.util.Set;

import com.viapath.certificatetracker.certificate.CertificateInventoryService;
import com.viapath.certificatetracker.certificate.CertificateInventoryService.CreateCertificate;
import com.viapath.certificatetracker.certificate.ExpirationHealth;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/certificates")
@SessionAttributes("certificateWizard")
@Profile({"dev", "qa", "test"})
public class CertificateWebController {
    private final CertificateInventoryService inventory;

    public CertificateWebController(CertificateInventoryService inventory) { this.inventory = inventory; }

    @ModelAttribute("certificateWizard")
    AddCertificateWizardForm wizard() { return new AddCertificateWizardForm(); }

    @GetMapping
    String inventory(@RequestParam(required = false) String q, @RequestParam(required = false) Long customer,
            @RequestParam(required = false) String health, @RequestParam(required = false) Long system,
            Model model) {
        ExpirationHealth parsedHealth = null;
        if (health != null && !health.isBlank()) {
            try { parsedHealth = ExpirationHealth.valueOf(health); }
            catch (IllegalArgumentException ignored) { model.addAttribute("filterWarning", "Unknown expiration status was ignored."); }
        }
        model.addAttribute("certificates", inventory.searchCertificates(q, customer, parsedHealth, system));
        model.addAttribute("filters", new SearchFilters(q, customer, health, system));
        model.addAttribute("options", inventory.wizardOptions(null));
        model.addAttribute("healthOptions", ExpirationHealth.values());
        return "certificates/list";
    }

    @GetMapping("/{id:\\d+}")
    String detail(@PathVariable long id, Model model) {
        model.addAttribute("certificate", inventory.findCertificateDetail(id));
        return "certificates/detail";
    }

    @GetMapping("/new")
    String start(SessionStatus status) { status.setComplete(); return "redirect:/certificates/new/customer"; }

    @GetMapping("/new/customer")
    String customer(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, Model model) {
        options(model, form); return "certificates/add/customer";
    }

    @PostMapping("/new/customer")
    String customerPost(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, BindingResult errors, Model model) {
        if (form.getCustomerId() == null) errors.addError(new FieldError("certificateWizard", "customerId", "Select a customer."));
        else try { inventory.customerName(form.getCustomerId()); } catch (IllegalArgumentException ex) { errors.addError(new FieldError("certificateWizard", "customerId", "Select an active customer.")); }
        if (errors.hasErrors()) { options(model, form); return "certificates/add/customer"; }
        return "redirect:/certificates/new/details";
    }

    @GetMapping("/new/details")
    String details(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, Model model) {
        if (form.getCustomerId() == null) return "redirect:/certificates/new/customer";
        options(model, form); return "certificates/add/details";
    }

    @PostMapping("/new/details")
    String detailsPost(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, BindingResult errors, Model model) {
        if (blank(form.getName())) errors.addError(new FieldError("certificateWizard", "name", "Certificate name is required."));
        if (blank(form.getCertificateTypeCode())) errors.addError(new FieldError("certificateWizard", "certificateTypeCode", "Certificate type is required."));
        if (form.getExpirationDate() == null) errors.addError(new FieldError("certificateWizard", "expirationDate", "Expiration date is required."));
        if (errors.hasErrors()) { options(model, form); return "certificates/add/details"; }
        return "redirect:/certificates/new/usage";
    }

    @GetMapping("/new/usage")
    String usage(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, Model model) {
        if (form.getCustomerId() == null) return "redirect:/certificates/new/customer";
        options(model, form); return "certificates/add/usage";
    }

    @PostMapping("/new/usage")
    String usagePost(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, BindingResult errors, Model model) {
        if (form.getFunctionUsageCodes().isEmpty()) errors.addError(new FieldError("certificateWizard", "functionUsageCodes", "Select at least one Function / Usage."));
        if (errors.hasErrors()) { options(model, form); return "certificates/add/usage"; }
        return "redirect:/certificates/new/where-used";
    }

    @GetMapping("/new/where-used")
    String whereUsed(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, Model model) {
        if (form.getCustomerId() == null) return "redirect:/certificates/new/customer";
        options(model, form); return "certificates/add/where-used";
    }

    @PostMapping("/new/where-used")
    String whereUsedPost(@ModelAttribute("certificateWizard") AddCertificateWizardForm form) {
        form.mergeHostnameInput(); return "redirect:/certificates/new/review";
    }

    @GetMapping("/new/review")
    String review(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, Model model) {
        if (form.getCustomerId() == null || blank(form.getName()) || form.getFunctionUsageCodes().isEmpty()) return "redirect:/certificates/new/customer";
        options(model, form); return "certificates/add/review";
    }

    @PostMapping("/new/complete")
    String complete(@ModelAttribute("certificateWizard") AddCertificateWizardForm form, BindingResult errors,
            Model model, SessionStatus status, RedirectAttributes redirect) {
        try {
            var created = inventory.createCertificate(new CreateCertificate(form.getCustomerId(), form.getName(),
                    form.getCertificateTypeCode(), form.getExpirationDate(), form.getIssuer(), form.getSerialNumber(),
                    form.getFingerprint(), form.getSubjectCommonName(), form.getNotes(), form.getFunctionUsageCodes(),
                    form.getSystemIds(), form.getEnvironmentCodes(), form.getHostnames(), form.getSiteIds()));
            status.setComplete();
            redirect.addFlashAttribute("successMessage", form.getName() + " has been added to Certificate Tracker.");
            return "redirect:/certificates/" + created.getId();
        } catch (IllegalArgumentException | jakarta.validation.ConstraintViolationException ex) {
            errors.reject("certificate.invalid", "The certificate could not be added. Review stale or invalid selections.");
            options(model, form); return "certificates/add/review";
        } catch (DataAccessException ex) {
            errors.reject("certificate.database", "The certificate could not be saved. No changes were committed.");
            options(model, form); return "certificates/add/review";
        }
    }

    @PostMapping("/new/cancel")
    String cancel(SessionStatus status, RedirectAttributes redirect) {
        status.setComplete(); redirect.addFlashAttribute("infoMessage", "Add Certificate was cancelled."); return "redirect:/certificates";
    }

    private void options(Model model, AddCertificateWizardForm form) {
        var options = inventory.wizardOptions(form.getCustomerId());
        model.addAttribute("options", options);
        model.addAttribute("selectedCustomer", label(options.customers(), form.getCustomerId() == null ? null : form.getCustomerId().toString()));
        model.addAttribute("selectedType", label(options.certificateTypes(), form.getCertificateTypeCode()));
        model.addAttribute("selectedUsages", labels(options.functionUsages(), form.getFunctionUsageCodes()));
        model.addAttribute("selectedSystems", labels(options.systems(), form.getSystemIds().stream().map(String::valueOf).collect(java.util.stream.Collectors.toSet())));
        model.addAttribute("selectedEnvironments", labels(options.environments(), form.getEnvironmentCodes()));
        model.addAttribute("selectedSites", labels(options.sites(), form.getSiteIds().stream().map(String::valueOf).collect(java.util.stream.Collectors.toSet())));
    }
    private static String label(java.util.List<CertificateInventoryService.Option> options, String value) {
        return options.stream().filter(option -> option.value().equals(value)).map(CertificateInventoryService.Option::label).findFirst().orElse("Unavailable selection");
    }
    private static java.util.List<String> labels(java.util.List<CertificateInventoryService.Option> options, Set<String> selected) {
        return options.stream().filter(option -> selected.contains(option.value())).map(CertificateInventoryService.Option::label).toList();
    }
    private static boolean blank(String value) { return value == null || value.isBlank(); }
    public record SearchFilters(String q, Long customer, String health, Long system) {}
}
