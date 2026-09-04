package com.viapath.certificatetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Temporary foundation-only access policy. Replace after the user and role model is governed. */
@Configuration
public class FoundationSecurityConfiguration {

    @Bean
    SecurityFilterChain foundationSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/css/**", "/error").permitAll()
                        .anyRequest().denyAll())
                .build();
    }
}
