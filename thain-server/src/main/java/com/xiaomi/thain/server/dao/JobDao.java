package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.core.model.dr.JobDr;
import com.xiaomi.thain.server.mapper.JobMapper;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

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

    @Nullable
    public JobDr getJobByFlowIdAndName(long flowId, @NonNull String name) {
        return jobMapper.getJobByFlowIdAndName(flowId, name);
    }


    public void updateJobProperties(long id, @NonNull String properties) {
        jobMapper.updateJobProperties(id, properties);
    }
}
