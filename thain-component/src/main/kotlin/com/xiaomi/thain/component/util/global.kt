package com.xiaomi.thain.component.util

import com.xiaomi.thain.common.utils.ifNull

fun formatHttpReferenceData(referenceData: String?,
                            getStorageValueOrDefault: (String, String, Any) -> Any): Map<String, String> {
    return referenceData.ifNull { "" }
            .split(",".toRegex())
            .map { it.trim() }
            .map { it.split("[:.]".toRegex()) }
            .filter { it.size == 3 }
            .map { it[0] to getStorageValueOrDefault(it[1], it[2], "").toString() }
            .toMap()
}
