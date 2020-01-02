package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.core.ThainFacade
import com.xiaomi.thain.core.model.rq.AddFlowRq
import com.xiaomi.thain.core.model.rq.AddJobRq
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import java.util.HashSet

/**
 * Date 19-5-27 下午5:55
 *
 * @author liangyongrui@xiaomi.com
 */
@Service
class CheckService(private val thainFacade: ThainFacade) {

    /**
     * 检查flowModel是否合法，不合法则抛出异常
     *
     * @param addFlowRq addFlowRq
     * @throws ThainException 不合法的异常
     */
    @Throws(ThainException::class)
    fun checkFlowModel(addFlowRq: AddFlowRq) {
        if (StringUtils.isBlank(addFlowRq.name)) {
            throw ThainException("flow name is empty")
        }
        if (StringUtils.isBlank(addFlowRq.createUser)) {
            throw ThainException("failed to obtain createUser")
        }
    }

    @Throws(ThainException::class)
    fun checkJobModelList(jobModelList: List<AddJobRq>) {
        if (jobModelList.isEmpty()) {
            throw ThainException("flow requires at least one job")
        }
        val jobNameSet = HashSet<String>()
        for ((name) in jobModelList) {
            if (name.length < 2) {
                throw ThainException("job name at least two characters ：$name")
            }
            if (!jobNameSet.add(name)) {
                throw ThainException("duplicated job name：$name")
            }
        }
        jobModelList.forEach { jobModel ->
            checkJobModel(jobModel)
            jobModel.condition?.split("&&|\\|\\|".toRegex())
                    ?.map { it.trim() }
                    ?.map {
                        val end = it.indexOf('.')
                        if (end < 0) it else it.substring(0, end)
                    }
                    ?.filter { it.isNotEmpty() }
                    ?.forEach {
                        if (!jobNameSet.contains(it)) {
                            throw ThainRuntimeException(jobModel.name + "relies on non-existent node: " + it)
                        }
                        if (jobModel.name == it) {
                            throw ThainRuntimeException(jobModel.name + "relies on himself")
                        }
                    }
        }
    }

    @Throws(ThainException::class)
    private fun checkJobModel(addJobRq: AddJobRq) {
        if (StringUtils.isBlank(addJobRq.name)) {
            throw ThainException("Some node name is empty")
        }
        if (!addJobRq.name.matches("^[_A-Za-z][_A-Za-z0-9]*$".toRegex())) {
            throw ThainException("Job names can only have numbers, letters, underscores, and begin with numbers or letters")
        }
        val componentDefineMap = thainFacade.componentService.componentDefineModels
        val componentDefine = componentDefineMap[addJobRq.component]
                ?: throw ThainException("Component of node " + addJobRq.name + " does not available ")
        val inputProperties = addJobRq.properties.filter { p -> p.value.isNotBlank() }.keys
        componentDefine.items
                .filter { it.required }
                .map { it.property }
                .forEach {
                    if (!inputProperties.contains(it)) {
                        throw ThainException("Required items of " + addJobRq.name + " not filled in：" + it)
                    }
                }
    }
}
