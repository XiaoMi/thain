/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.executor.service;

import com.xiaomi.thain.common.constant.JobExecutionStatus;
import com.xiaomi.thain.core.constant.LogLevel;
import com.xiaomi.thain.core.dao.JobExecutionDao;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.runtime.log.JobExecutionLogHandler;
import lombok.Getter;
import lombok.NonNull;


/**
 * jobExecution服务类，对不影响任务执行的方法进行管理，如：日志,状态等
 *
 * @author liangyongrui@xiaomi.com
 */
public class JobExecutionService {

    @NonNull
    private final JobExecutionLogHandler jobExecutionLogHandler;
    @NonNull
    private final JobExecutionDao jobExecutionDao;
    private final long jobExecutionId;
    @NonNull
    private final String jobExecutionName;

    /**
     * 如果是异常结束,异常信息.
     * 正常结束时，errorMessage为""
     */
    @Getter
    private String errorMessage = "";

    /**
     * 流程结束状态
     */
    @Getter
    @NonNull
    private JobExecutionStatus endStatus = JobExecutionStatus.SUCCESS;

    private JobExecutionService(long jobExecutionId,
                                @NonNull String jobExecutionName,
                                @NonNull ProcessEngineStorage processEngineStorage) {
        this.jobExecutionLogHandler = JobExecutionLogHandler.getInstance(jobExecutionId, processEngineStorage);
        this.jobExecutionDao = processEngineStorage.getJobExecutionDao();
        this.jobExecutionId = jobExecutionId;
        this.jobExecutionName = jobExecutionName;
    }

    public static JobExecutionService getInstance(long jobExecutionId, @NonNull String jobExecutionName,
                                                  @NonNull ProcessEngineStorage processEngineStorage) {
        return new JobExecutionService(jobExecutionId, jobExecutionName, processEngineStorage);
    }

    public void startJobExecution() {
        jobExecutionLogHandler.add("begin execute node：" + jobExecutionName, LogLevel.INFO);
        jobExecutionDao.updateCreateTimeAndStatus(jobExecutionId, JobExecutionStatus.RUNNING);
    }

    public void endJobExecution() {
        try {
            switch (endStatus) {
                case ERROR:
                    jobExecutionLogHandler.add("executed abort with：" + errorMessage, LogLevel.ERROR);
                    break;
                case SUCCESS:
                default:
                    jobExecutionLogHandler.add("executed completed", LogLevel.INFO);
            }
        } finally {
            jobExecutionDao.updateStatus(jobExecutionId, endStatus);
            jobExecutionLogHandler.close();
        }
    }

    public void addError(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
        endStatus = JobExecutionStatus.ERROR;
    }
}
