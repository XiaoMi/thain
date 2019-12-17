package com.xiaomi.thain.common.model


/**
 * 组件定义
 */
data class ComponentDefine(
        val group: String,
        val name: String,
        val hidden: Boolean,
        val items: List<Item>
) {
    data class Item(
            /**
             * 属性key
             */
            val property: String,
            val required: Boolean = false,
            val label: String,
            val input: Input) {

        data class Input(val id: String,
                         /**
                          * 只有当id为select时 options才存在
                          */
                         val options: List<Option>?) {
            data class Option(val id: String, val name: String?)
        }
    }
}
