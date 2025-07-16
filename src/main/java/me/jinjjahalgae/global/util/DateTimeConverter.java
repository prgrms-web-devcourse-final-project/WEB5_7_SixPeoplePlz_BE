package me.jinjjahalgae.global.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

// Instant <-> LocalDateTime 변환 유틸 (Instant를 우선적으로 사용하세요)
public class DateTimeConverter {
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
} 