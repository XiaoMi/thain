/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.mapper;

import com.xiaomi.thain.common.model.JobExecutionModel;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
public interface JobExecutionMapper {

    int add(@NonNull JobExecutionModel jobExecutionModel);

    int updateLogs(@Param("jobExecutionId") long jobExecutionId, @NonNull @Param("logs") String logs);

    int updateStatus(@Param("jobExecutionId") long jobExecutionId, @Param("status") int status);

    int updateCreateTime(@Param("jobExecutionId") long jobExecutionId);

    /**
     * 获取所有需要删除的Job execution id
     * <p>
     * 需要删除：flow execution Id 不在列表中， 并且最后一次更新时间大于一小时
     */
    List<Long> getNeedDeleteJobExecutionIds(@NonNull List<Long> flowExecutionIds);

    int deleteJobExecutionByIds(@NonNull List<Long> needDeleteJobExecutionIds);

    int killJobExecution(long flowExecutionId);
}
