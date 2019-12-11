package com.xiaomi.thain.common.utils

inline infix fun <T : Any> T?.ifNull(block: (T?) -> T): T {
    if (this == null) {
        return block(this)
    }
    return this
}
