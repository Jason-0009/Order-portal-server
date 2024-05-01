package com.order.portal.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.data.util.Pair;

public class TimeUtils {
    public static Pair<Instant, Instant> calculateStartAndEndOfDay(Instant date) {
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime dateTimeInUserZone = date.atZone(zoneId);

        Instant startOfDay = dateTimeInUserZone
                .toLocalDate()
                .atStartOfDay(dateTimeInUserZone.getZone())
                .toInstant();

        Instant endOfDay = dateTimeInUserZone
                .toLocalDate()
                .atStartOfDay(dateTimeInUserZone.getZone())
                .plusDays(1)
                .minusNanos(1)
                .toInstant();

        return Pair.of(startOfDay, endOfDay);
    }
}
