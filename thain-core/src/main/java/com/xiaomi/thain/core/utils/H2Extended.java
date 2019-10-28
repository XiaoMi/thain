package com.xiaomi.thain.core.utils;

import lombok.NonNull;

/**
 * @author liangyongrui
 */
public class H2Extended {
    public static int unixTimestamp(@NonNull java.sql.Timestamp timestamp) {
        return (int) (timestamp.getTime() / 1000L);
    }
}