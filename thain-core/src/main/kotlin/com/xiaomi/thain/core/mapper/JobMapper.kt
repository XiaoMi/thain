package com.xiaomi.thain.core.mapper

import com.xiaomi.thain.core.model.dr.JobDr
import org.apache.ibatis.annotations.Param

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
interface JobMapper {

    fun getJobs(@Param("flowId") flowId: Long): List<JobDr>

    fun cleanUpExpiredAndDeletedJob(dataReserveDays: Int): Int
}
