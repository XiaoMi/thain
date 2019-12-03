/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException;
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.dr.FlowDr;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.common.model.rq.AddFlowRq;
import com.xiaomi.thain.common.model.rq.UpdateFlowRq;
import com.xiaomi.thain.core.ThainFacade;
import com.xiaomi.thain.core.config.DatabaseHandler;
import com.xiaomi.thain.core.dao.*;
import com.xiaomi.thain.core.process.runtime.FlowExecutionLoader;
import com.xiaomi.thain.core.process.runtime.heartbeat.FlowExecutionHeartbeat;
import com.xiaomi.thain.core.process.service.ComponentService;
import com.xiaomi.thain.core.process.service.MailService;
import com.xiaomi.thain.core.thread.pool.ThainThreadPool;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.LongFunction;

import static com.xiaomi.thain.common.constant.FlowSchedulingStatus.NOT_SET;
import static com.xiaomi.thain.common.constant.FlowSchedulingStatus.SCHEDULING;

/**
 * Date 19-5-17 下午2:09
 *
 * @author liangyongrui@xiaomi.com
 */
@EqualsAndHashCode
@Slf4j
public class ProcessEngine {

    @NonNull
    public final String processEngineId;
    @NonNull
    public final ProcessEngineStorage processEngineStorage;
    @NonNull
    public final ThainFacade thainFacade;
    @NonNull
    public final FlowExecutionLoader flowExecutionLoader;

    private static final Map<String, ProcessEngine> PROCESS_ENGINE_MAP = new ConcurrentHashMap<>();

    private ProcessEngine(@NonNull ProcessEngineConfiguration processEngineConfiguration, @NonNull ThainFacade thainFacade)
            throws ThainMissRequiredArgumentsException, SQLException, IOException, InterruptedException {
        this.thainFacade = thainFacade;
        this.processEngineId = UUID.randomUUID().toString();
        PROCESS_ENGINE_MAP.put(processEngineId, this);

        LongFunction<ThainThreadPool> flowExecutionJobExecutionThreadPool = flowExecutionId -> ThainThreadPool.getInstance(
                "thain-job-execution-thread[flowExecutionId:" + flowExecutionId + "]",
                processEngineConfiguration.flowExecutionJobExecutionThreadPoolCoreSize);
        val flowExecutionThreadPool = ThainThreadPool.getInstance("thain-flow-execution-thread",
                processEngineConfiguration.flowExecutionThreadPoolCoreSize);

        val sqlSessionFactory = DatabaseHandler.getSqlSessionFactory(processEngineConfiguration.dataSource);

        switch (processEngineConfiguration.initLevel) {
            case "1":
                createTable(processEngineConfiguration.dataSource.getConnection());
                initData(processEngineConfiguration.dataSource.getConnection());
                break;
            case "2":
                initData(processEngineConfiguration.dataSource.getConnection());
                break;
            default:
        }


        val userDao = UserDao.getInstance(sqlSessionFactory);

        val mailService = MailService.getInstance(processEngineConfiguration.mailHost,
                processEngineConfiguration.mailSender,
                processEngineConfiguration.mailSenderUsername,
                processEngineConfiguration.mailSenderPassword,
                userDao);

        val flowDao = FlowDao.getInstance(sqlSessionFactory, mailService);
        val flowExecutionDao = FlowExecutionDao.getInstance(sqlSessionFactory, mailService, processEngineConfiguration.dataReserveDays);
        val jobDao = JobDao.getInstance(sqlSessionFactory, mailService);
        val jobExecutionDao = JobExecutionDao.getInstance(sqlSessionFactory, mailService);
        val componentService = ComponentService.getInstance();

        val flowExecutionWaitingQueue = new LinkedBlockingQueue<FlowExecutionDr>();


        processEngineStorage = ProcessEngineStorage.builder()
                .flowExecutionJobExecutionThreadPool(flowExecutionJobExecutionThreadPool)
                .flowExecutionThreadPool(flowExecutionThreadPool)
                .processEngineId(processEngineId)
                .flowDao(flowDao)
                .flowExecutionDao(flowExecutionDao)
                .jobDao(jobDao)
                .jobExecutionDao(jobExecutionDao)
                .mailService(mailService)
                .componentService(componentService)
                .flowExecutionWaitingQueue(flowExecutionWaitingQueue)
                .build();

        this.flowExecutionLoader = FlowExecutionLoader.getInstance(processEngineStorage);
        val flowExecutionHeartbeat = FlowExecutionHeartbeat.getInstance(flowExecutionDao, mailService);
        flowExecutionHeartbeat.addCollections(flowExecutionWaitingQueue);
        flowExecutionHeartbeat.addCollections(flowExecutionLoader.runningFlowExecution);

    }

    private void createTable(@NonNull Connection connection) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection);
        val driver = DriverManager.getDriver(connection.getMetaData().getURL()).getClass().getName();
        if ("org.h2.Driver".equals(driver)) {
            runner.runScript(Resources.getResourceAsReader("sql/h2/quartz.sql"));
            runner.runScript(Resources.getResourceAsReader("sql/h2/thain.sql"));
        } else if ("com.mysql.cj.jdbc.Driver".equals(driver)) {
            runner.runScript(Resources.getResourceAsReader("sql/mysql/quartz.sql"));
            runner.runScript(Resources.getResourceAsReader("sql/mysql/spring_session.sql"));
            runner.runScript(Resources.getResourceAsReader("sql/mysql/thain.sql"));
        }
    }

    private void initData(@NonNull Connection connection) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection);
        val driver = DriverManager.getDriver(connection.getMetaData().getURL()).getClass().getName();
        if ("org.h2.Driver".equals(driver)) {
            runner.runScript(Resources.getResourceAsReader("sql/h2/init_data.sql"));
        } else if ("com.mysql.cj.jdbc.Driver".equals(driver)) {
            runner.runScript(Resources.getResourceAsReader("sql/mysql/init_data.sql"));
        }
    }

    /**
     * 用id获取流程实例
     */
    public static ProcessEngine getInstance(@NonNull String processEngineId) {
        return Optional.ofNullable(PROCESS_ENGINE_MAP.get(processEngineId)).orElseThrow(
                () -> new ThainRuntimeException("Failed to obtain process instance"));
    }

    public static ProcessEngine newInstance(@NonNull ProcessEngineConfiguration processEngineConfiguration,
                                            @NonNull ThainFacade thainFacade)
            throws ThainMissRequiredArgumentsException, IOException, SQLException, InterruptedException {
        return new ProcessEngine(processEngineConfiguration, thainFacade);
    }

    /**
     * 插入flow
     * 成功返回 flow id
     */
    public Optional<Long> addFlow(@NonNull AddFlowRq addFlowRq, @NonNull List<JobModel> jobModelList) {
        try {
            int schedulingStatus = NOT_SET.code;
            if (StringUtils.isNotBlank(addFlowRq.cron)) {
                schedulingStatus = SCHEDULING.code;
            }
            val cloneFlowModel = addFlowRq.toBuilder().schedulingStatus(schedulingStatus).build();
            processEngineStorage.flowDao.addFlow(cloneFlowModel, jobModelList);
            return Optional.ofNullable(cloneFlowModel.id);
        } catch (Exception e) {
            log.error("addFlow:", e);
        }
        return Optional.empty();
    }

    public void updateFlow(@NonNull UpdateFlowRq updateFlowRq, @NonNull List<JobModel> jobModelList) {
        processEngineStorage.flowDao.updateFlow(updateFlowRq, jobModelList);
    }

    /**
     * 删除flow
     * 返回删除job个数
     */
    public void deleteFlow(long flowId) {
        processEngineStorage.flowDao.deleteFlow(flowId);
    }

    /**
     * 手动触发一次
     */
    public long startProcess(long flowId) throws ThainException, ThainRepeatExecutionException {
        return flowExecutionLoader.startAsync(flowId);
    }

    public FlowDr getFlow(long flowId) throws ThainException {
        return processEngineStorage.flowDao
                .getFlow(flowId).orElseThrow(() -> new ThainException("failed to obtain flow"));

    }
}
