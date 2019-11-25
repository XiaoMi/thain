package com.xiaomi.thain.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;

/**
 * @author liangyongrui
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class H2Extended {

    public static int unixTimestamp(@NonNull java.sql.Timestamp timestamp) {
        return (int) (timestamp.getTime() / 1000L);
    }

    public static String fromUnixTime(@NonNull long timestamp) {
        val instant = Instant.ofEpochSecond(timestamp);
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(UTC)
                .format(instant);
    }
}