package me.jinjjahalgae.global.util;

import java.time.Instant;
import java.time.LocalDate;
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

    /**
     * UTC 기준 현재 시간을 Instant로 반환
     * @return Instant
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Instant를 UTC 기준 LocalDateTime으로 변환
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * UTC 기준 현재 시간을 LocalDateTime으로 반환
     * @return LocalDateTime
     */
    public static LocalDateTime nowAsLocalDateTime() {
        return toLocalDateTime(now());
    }

    /**
     * UTC 기준 현재 날짜를 LocalDate로 반환
     * @return LocalDate
     */
    public static LocalDate nowAsLocalDate() {
        return nowAsLocalDateTime().toLocalDate();
    }
} 