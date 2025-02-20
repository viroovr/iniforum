package com.forum.project.core.common;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ClockUtil {
    private static Clock clock = Clock.systemDefaultZone();

    public static LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public static void setFixedClock(String isoDateTime, String zoneId) {
        clock = Clock.fixed(Instant.parse(isoDateTime), ZoneId.of(zoneId));
    }

    public static void resetClock() {
        clock = Clock.systemDefaultZone();
    }
}
