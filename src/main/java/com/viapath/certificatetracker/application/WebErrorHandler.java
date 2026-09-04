package com.viapath.certificatetracker.application;

import com.viapath.certificatetracker.certificate.CertificateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WebErrorHandler {
    @ExceptionHandler(CertificateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String certificateNotFound(Model model) {
        model.addAttribute("errorTitle", "Certificate not found");
        model.addAttribute("errorMessage", "The requested certificate does not exist or is no longer available.");
        return "error/operational";
    }
}
