package com.xiaomi.thain.common.utils

import java.net.InetAddress

/**
 * @author liangyongrui
 */
object HostUtils {
    val hostInfo: String
        get() = try {
            InetAddress.getLocalHost().toString()
        } catch (e: Exception) {
            "unknown"
        }
}
