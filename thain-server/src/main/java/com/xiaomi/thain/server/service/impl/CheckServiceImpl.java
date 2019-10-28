/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.thain.common.entity.ComponentItemDefine;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.core.ThainFacade;
import com.xiaomi.thain.server.service.CheckService;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Date 19-5-27 下午5:55
 *
 * @author liangyongrui@xiaomi.com
 */
@Service
public class CheckServiceImpl implements CheckService {

    @NonNull
    private final ThainFacade thainFacade;

    public CheckServiceImpl(@NonNull ThainFacade thainFacade) {
        this.thainFacade = thainFacade;
    }

    @Override
    public void checkFlowModel(@NonNull FlowModel flowModel) throws ThainException {
        if (StringUtils.isBlank(flowModel.name)) {
            throw new ThainException("flow name is empty");
        }
        if (StringUtils.isBlank(flowModel.createUser)) {
            throw new ThainException("failed to obtain createUser");
        }
    }

    @Override
    public void checkJobModelList(@NonNull List<JobModel> jobModelList) throws ThainException {
        if (jobModelList.isEmpty()) {
            throw new ThainException("job node is empty");
        }
        val jobNameSet = new HashSet<String>();
        for (val jobModel : jobModelList) {
            if (!jobNameSet.add(jobModel.name)) {
                throw new ThainException("duplicated job node name：" + jobModel.name);
            }
        }
        for (val jobModel : jobModelList) {
            checkJobModel(jobModel);
            Optional.ofNullable(jobModel.condition)
                    .map(t -> t.split("&&|\\|\\|"))
                    .map(Arrays::stream).orElseGet(Stream::empty)
                    .map(String::trim)
                    .map(t -> {
                        val end = t.indexOf('.');
                        return end < 0 ? t : t.substring(0, end);
                    })
                    .filter(t -> t.length() > 0)
                    .forEach(t -> {
                        if (!jobNameSet.contains(t)) {
                            throw new ThainRuntimeException(jobModel.name + "relies on non-existent node: " + t);
                        }
                        if (jobModel.name.equals(t)) {
                            throw new ThainRuntimeException(jobModel.name + "relies on himself");
                        }
                    });
        }
    }

    private void checkJobModel(@NonNull JobModel jobModel) throws ThainException {
        if (StringUtils.isBlank(jobModel.name)) {
            throw new ThainException("Some node name is empty");
        }
        if (!jobModel.name.matches("^[_A-Za-z][_A-Za-z0-9]*$")) {
            throw new ThainException("Job names can only have numbers, letters, underscores, and begin with numbers or letters");
        }
        val componentDefineMap = getComponentDefineMap();
        val componentDefine = Optional.ofNullable(componentDefineMap.get(jobModel.component))
                .orElseThrow(() -> new ThainException("Component of node " + jobModel.name + " does not available "));

        out:
        for (val item : componentDefine) {
            if (item.required) {
                for (val prop : jobModel.properties.keySet()) {
                    if (item.property.equals(prop)) {
                        continue out;
                    }
                }
                throw new ThainException("Required items of " + jobModel.name + " not filled in：" + item.property);
            }
        }
    }

    private Map<String, List<ComponentItemDefine>> getComponentDefineMap() {
        Gson gson = new Gson();
        return thainFacade.getComponentDefineJsonList().entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        t -> gson.fromJson(t.getValue(), new TypeToken<List<ComponentItemDefine>>() {
                        }.getType()))
                );
    }
}
