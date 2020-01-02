package com.xiaomi.thain.server.service

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.common.model.rq.UpdateJobPropertiesRq
import com.xiaomi.thain.server.dao.JobDao
import org.springframework.stereotype.Service

/**
 * @author liangyongrui
 */
@Service
class JobService(private val jobDao: JobDao) {
    /**
     * 更新指定job 的properties
     * 如果key存在就更新，不存在就追加
     */
    fun updateJobProperties(updateJobPropertiesRq: UpdateJobPropertiesRq) {
        val job = jobDao.getJobByFlowIdAndName(updateJobPropertiesRq.flowId, updateJobPropertiesRq.jobName)
                ?: throw ThainRuntimeException("job不存在")
        val properties = HashMap(job.properties)
        properties.putAll(updateJobPropertiesRq.modifyProperties)
        jobDao.updateJobProperties(job.id, JSON.toJSONString(properties))
    }

}
