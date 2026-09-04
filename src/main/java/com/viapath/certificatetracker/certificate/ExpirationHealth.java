package com.viapath.certificatetracker.certificate;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public enum ExpirationHealth {
    ACTIVE, EXPIRING_90, EXPIRING_60, EXPIRING_30, EXPIRED;

    public static final ZoneId BUSINESS_ZONE = ZoneId.of("America/New_York");

    public static ExpirationHealth calculate(LocalDate expirationDate, Clock clock) {
        if (expirationDate == null) throw new IllegalArgumentException("Expiration date is required");
        var today = LocalDate.now(clock.withZone(BUSINESS_ZONE));
        long days = ChronoUnit.DAYS.between(today, expirationDate);
        if (days < 0) return EXPIRED;
        if (days <= 30) return EXPIRING_30;
        if (days <= 60) return EXPIRING_60;
        if (days <= 90) return EXPIRING_90;
        return ACTIVE;
    }
}
