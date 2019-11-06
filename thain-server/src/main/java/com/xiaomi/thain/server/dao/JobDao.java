package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.server.mapper.JobMapper;
import com.xiaomi.thain.server.model.dr.JobDr;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author liangyongrui
 */
@Component
public class JobDao {

    @NonNull
    private final JobMapper jobMapper;

    public JobDao(@NonNull JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    public Optional<JobDr> getJobByFlowIdAndName(long flowId, @NonNull String name) {
        return jobMapper.getJobByFlowIdAndName(flowId, name);
    }


    public void updateJobProperties(long id, @NonNull String properties) {
        jobMapper.updateJobProperties(id, properties);
    }
}
