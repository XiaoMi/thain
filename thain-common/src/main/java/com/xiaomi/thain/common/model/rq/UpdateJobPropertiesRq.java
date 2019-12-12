package com.xiaomi.thain.common.model.rq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.Map;

/**
 * @author liangyongrui
 */
@AllArgsConstructor
@Builder
public class UpdateJobPropertiesRq {
    @NonNull
    public final Long flowId;
    @NonNull
    public final String jobName;
    @NonNull
    public final Map<String, String> modifyProperties;
}
