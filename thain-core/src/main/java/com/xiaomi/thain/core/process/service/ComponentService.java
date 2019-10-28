/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.service;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.component.annotation.ThainComponent;
import com.xiaomi.thain.core.utils.ReflectUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class ComponentService {

    @NonNull
    private final Map<String, Class<?>> components;
    @NonNull
    private final Map<String, String> componentJson;

    private ComponentService() {
        this.components = new HashMap<>();
        this.componentJson = new HashMap<>();
        initComponent();
    }

    public static ComponentService getInstance() {
        return new ComponentService();
    }

    /**
     * 加载Component
     */
    private void initComponent() {
        ReflectUtils.getClassesByAnnotation("com.xiaomi.thain.component", ThainComponent.class)
                .forEach(t -> {
                    val group = t.getAnnotation(ThainComponent.class).group();
                    val name = t.getAnnotation(ThainComponent.class).name();
                    val defineJson = t.getAnnotation(ThainComponent.class).defineJson();
                    components.put(group + "::" + name, t);
                    componentJson.put(group + "::" + name, defineJson);
                });
    }

    /**
     * 获取组件的定义Map，用于前端展示
     */
    public Map<String, String> getComponentDefineJsonList() {
        return ImmutableMap.copyOf(componentJson);
    }

    public Optional<Class<?>> getComponentClass(@NonNull String componentFullName) {
        return Optional.ofNullable(components.get(componentFullName));
    }
}
