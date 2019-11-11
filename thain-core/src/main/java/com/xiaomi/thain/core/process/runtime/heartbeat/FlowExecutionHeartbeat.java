package com.xiaomi.thain.core.process.runtime.heartbeat;

import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.core.dao.FlowExecutionDao;
import com.xiaomi.thain.core.process.service.MailService;
import com.xiaomi.thain.core.thread.pool.ThainThreadPool;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 定时发送心跳
 *
 * @author liangyongrui
 */
@Log4j2
public class FlowExecutionHeartbeat {

    private final Set<Collection<FlowExecutionDr>> collections = Collections.newSetFromMap(new IdentityHashMap<>());
    @NonNull
    private final FlowExecutionDao flowExecutionDao;
    @NonNull
    private final MailService mailService;

    public void addCollections(Collection<FlowExecutionDr> collection) {
        collections.add(collection);
    }

    public static FlowExecutionHeartbeat getInstance(@NonNull FlowExecutionDao flowExecutionDao, @NonNull MailService mailService) {
        return new FlowExecutionHeartbeat(flowExecutionDao, mailService);
    }

    private FlowExecutionHeartbeat(@NonNull FlowExecutionDao flowExecutionDao, @NonNull MailService mailService) {
        this.flowExecutionDao = flowExecutionDao;
        this.mailService = mailService;
        log.info("init FlowExecutionHeartbeat");
        ThainThreadPool.DEFAULT_THREAD_POOL.execute(this::sendHeartbeat);
    }

    /**
     * 每30s发送一次心跳
     */
    private void sendHeartbeat() {
        while (true) {
            try {
                val flowExecutionIds = collections.stream().flatMap(Collection::stream)
                        .map(t -> t.id).collect(Collectors.toList());
                flowExecutionDao.setFlowExecutionHeartbeat(flowExecutionIds);
                TimeUnit.SECONDS.sleep(30);
            } catch (Exception e) {
                mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
                log.error("", e);
            }
        }
    }

}
