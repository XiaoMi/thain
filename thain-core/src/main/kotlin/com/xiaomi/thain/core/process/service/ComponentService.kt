package com.xiaomi.thain.core.process.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.xiaomi.thain.common.model.ComponentDefine
import com.xiaomi.thain.component.annotation.ThainComponent
import com.xiaomi.thain.core.utils.ReflectUtils
import java.util.*

/**
 * @author liangyongrui@xiaomi.com
 */
class ComponentService {
    private val componentClass: Map<String, Class<*>>
    val componentDefineModels: Map<String, ComponentDefine>
    val componentDefineList: List<ComponentDefine>

    init {
        val componentClassList = ReflectUtils.getClassesByAnnotation(
                "com.xiaomi.thain.component", ThainComponent::class.java)

        val mutableComponentDefineModels = mutableMapOf<String, ComponentDefine>()
        val mutableComponentClass = mutableMapOf<String, Class<*>>()

        componentClassList.forEach { clazz ->
            val json = clazz.getAnnotation(ThainComponent::class.java).value
            val componentDefine = JSON.parseObject(json, object : TypeReference<ComponentDefine>() {})
            val fullName = "${componentDefine.group}:${componentDefine.name}"
            mutableComponentDefineModels[fullName] = componentDefine
            mutableComponentClass[fullName] = clazz
        }

        componentDefineModels = mutableComponentDefineModels
        componentClass = mutableComponentClass
        componentDefineList = mutableComponentDefineModels.values.toList()
    }

    fun getComponentClass(componentFullName: String): Optional<Class<*>> {
        return Optional.ofNullable(componentClass[componentFullName])
    }

}
