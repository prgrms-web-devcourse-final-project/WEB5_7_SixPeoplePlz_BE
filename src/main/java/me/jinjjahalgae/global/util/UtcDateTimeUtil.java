package me.jinjjahalgae.global.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UtcDateTimeUtil {
    private static final DateTimeFormatter ISO_UTC_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneOffset.UTC);

    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        
        return ISO_UTC_FORMATTER.format(localDateTime.atZone(ZoneOffset.UTC));
    }
} 