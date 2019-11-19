/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.process.runtime.executor;

import com.xiaomi.thain.common.exception.JobExecuteException;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.component.tools.ComponentTools;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.component.tools.impl.ComponentToolsImpl;
import com.xiaomi.thain.core.process.runtime.executor.service.JobExecutionService;
import com.xiaomi.thain.core.process.runtime.notice.HttpNotice;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;

/**
 * 节点执行类
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
public class JobExecutor {

    @NonNull
    private final JobModel jobModel;
    private final long jobExecutionModelId;
    private final long flowExecutionId;

    @NonNull
    private final ProcessEngineStorage processEngineStorage;
    @NonNull
    private final JobExecutionService jobExecutionService;
    @NonNull
    private final HttpNotice httpNotice;

    private JobExecutor(long flowExecutionId,
                        @NonNull JobModel jobModel,
                        @NonNull JobExecutionModel jobExecutionModel,
                        @NonNull ProcessEngineStorage processEngineStorage) {
        this.jobModel = jobModel;
        this.flowExecutionId = flowExecutionId;
        this.processEngineStorage = processEngineStorage;
        this.jobExecutionModelId = jobExecutionModel.id;
        this.jobExecutionService = JobExecutionService.getInstance(jobExecutionModelId, jobModel.name, processEngineStorage);
        this.httpNotice = HttpNotice.getInstance(jobModel.callbackUrl, jobModel.flowId, flowExecutionId);
    }

    /**
     * 执行job, 返回是否执行完成
     */
    public static void start(long flowExecutionId,
                             @NonNull JobModel jobModel,
                             @NonNull JobExecutionModel jobExecutionModel,
                             @NonNull ProcessEngineStorage processEngineStorage) throws JobExecuteException {
        val jobExecutor = new JobExecutor(flowExecutionId, jobModel, jobExecutionModel, processEngineStorage);
        jobExecutor.run();
    }

    private void run() throws JobExecuteException {
        try {
            jobExecutionService.startJobExecution();
            httpNotice.sendStart();
            execute();
            httpNotice.sendSuccess();
        } catch (Exception e) {
            jobExecutionService.addError("Abort with: " + ExceptionUtils.getRootCauseMessage(e));
            httpNotice.sendError(ExceptionUtils.getRootCauseMessage(e));
            log.warn(ExceptionUtils.getRootCauseMessage(e));
            throw new JobExecuteException(e);
        } finally {
            try {
                jobExecutionService.endJobExecution();
            } catch (Exception e) {
                try {
                    processEngineStorage.mailService.sendSeriousError(
                            "Failed to modify job status, detail message：" + ExceptionUtils.getStackTrace(e));
                } catch (Exception ex) {
                    log.error("", ex);
                }
            }
        }
    }

    /**
     * 执行组件
     */
    private void execute() throws ThainException {
        val clazz = processEngineStorage.componentService.getComponentClass(jobModel.component)
                .orElseThrow(() -> new ThainException("component does not exist"));
        try {
            val instance = clazz.getConstructor().newInstance();
            val fields = instance.getClass().getDeclaredFields();
            for (val field : fields) {
                field.setAccessible(true);
                if (ComponentTools.class.isAssignableFrom(field.getType())) {
                    field.set(instance, new ComponentToolsImpl(jobModel, jobExecutionModelId, flowExecutionId, processEngineStorage));
                    continue;
                }
                if (field.getType().isAssignableFrom(String.class)) {
                    val v = Optional.ofNullable(jobModel.properties.get(field.getName()));
                    if (v.isPresent()) {
                        field.set(instance, v.get());
                    }
                }
            }
            val method = clazz.getDeclaredMethod("run");
            method.setAccessible(true);
            method.invoke(instance);
        } catch (Exception e) {
            throw new JobExecuteException(e);
        }
    }

}
