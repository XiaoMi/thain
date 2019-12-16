package com.xiaomi.thain.common.utils

inline infix fun <T : Any> T?.ifNull(block: (T?) -> T): T {
    if (this == null) {
        return block(this)
    }
    return this
}

fun <T : Any> T?.isNull(): Boolean {
    return this == null
}

fun <T : Any> T?.isNotNull(): Boolean {
    return this != null
}

fun <T> List<T>.copyOf(): List<T> {
    return mutableListOf<T>().also { it.addAll(this) }
}
