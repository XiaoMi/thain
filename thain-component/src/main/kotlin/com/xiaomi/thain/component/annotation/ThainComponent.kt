package com.xiaomi.thain.component.annotation

import java.lang.annotation.Inherited

/**
 * Thain组件
 *
 * @author liangyongrui@xiaomi.com
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Inherited
annotation class ThainComponent(val value: String)
