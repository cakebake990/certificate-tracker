package com.viapath.certificatetracker.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExpirationHealthTests {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-04T16:00:00Z"), ZoneOffset.UTC);

    @ParameterizedTest
    @CsvSource({
        "2026-09-03, EXPIRED", "2026-09-04, EXPIRING_30", "2026-10-04, EXPIRING_30",
        "2026-10-05, EXPIRING_60", "2026-11-03, EXPIRING_60", "2026-11-04, EXPIRING_90",
        "2026-12-03, EXPIRING_90", "2026-12-04, ACTIVE"
    })
    void calculatesBoundaryHealth(LocalDate expiration, ExpirationHealth expected) {
        assertThat(ExpirationHealth.calculate(expiration, CLOCK)).isEqualTo(expected);
    }
}
