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
        interface Input
        data class CommonInput(val id: String) : Input
        data class SelectInput(val id: String, val options: List<Option>) : Input {
            data class Option(val id: String, val name: String)
        }
    }
}
