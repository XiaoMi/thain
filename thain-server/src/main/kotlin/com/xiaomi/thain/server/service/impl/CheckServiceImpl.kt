package com.xiaomi.thain.server.service.impl

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.core.ThainFacade
import com.xiaomi.thain.core.model.rq.AddFlowRq
import com.xiaomi.thain.core.model.rq.AddJobRq
import com.xiaomi.thain.server.service.CheckService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import java.util.HashSet

/**
 * Date 19-5-27 下午5:55
 *
 * @author liangyongrui@xiaomi.com
 */
@Service
class CheckServiceImpl(private val thainFacade: ThainFacade) : CheckService {
    @Throws(ThainException::class)
    override fun checkFlowModel(addFlowRq: AddFlowRq) {
        if (StringUtils.isBlank(addFlowRq.name)) {
            throw ThainException("flow name is empty")
        }
        if (StringUtils.isBlank(addFlowRq.createUser)) {
            throw ThainException("failed to obtain createUser")
        }
    }

    @Throws(ThainException::class)
    override fun checkJobModelList(jobModelList: List<AddJobRq>) {
        if (jobModelList.isEmpty()) {
            throw ThainException("job node is empty")
        }
        val jobNameSet = HashSet<String>()
        for ((name) in jobModelList) {
            if (!jobNameSet.add(name)) {
                throw ThainException("duplicated job node name：$name")
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

        componentDefine.items
                .filter { it.required }
                .map { it.property }
                .forEach {
                    if (!addJobRq.properties.keys.contains(it)) {
                        throw ThainException("Required items of " + addJobRq.name + " not filled in：" + it)
                    }
                }
    }
}
