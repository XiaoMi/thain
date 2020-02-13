package com.xiaomi.thain.common.utils;

import java.net.InetAddress;

/**
 * @author liangyongrui
 */
public class HostUtils {
    private HostUtils() {

    }

    public static String getHostInfo() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
