/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.rq.UpdateJobPropertiesRq;
import com.xiaomi.thain.server.dao.JobDao;
import com.xiaomi.thain.server.service.JobService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * @author liangyongrui
 */
@Service
@Slf4j
public class JobServiceImpl implements JobService {

    @NonNull
    private final JobDao jobDao;

    public JobServiceImpl(@NonNull JobDao jobDao) {
        this.jobDao = jobDao;
    }

    @Override
    public void updateJobProperties(@NonNull UpdateJobPropertiesRq updateJobPropertiesRq) {
        val job = jobDao.getJobByFlowIdAndName(updateJobPropertiesRq.flowId, updateJobPropertiesRq.jobName)
                .orElseThrow(() -> new ThainRuntimeException("job不存在"));
        val properties = JSON.parseObject(job.properties);
        properties.putAll(updateJobPropertiesRq.modifyProperties);
        jobDao.updateJobProperties(job.id, JSON.toJSONString(properties));
    }
}
