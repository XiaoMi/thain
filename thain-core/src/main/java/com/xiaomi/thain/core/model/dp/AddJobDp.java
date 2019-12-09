package com.xiaomi.thain.core.model.dp;

import com.xiaomi.thain.common.model.rq.kt.AddJobRq;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * @author liangyongrui
 */
@AllArgsConstructor
public class AddJobDp {
    @NonNull
    public final Long flowId;
    @NonNull
    public final String name;
    @Nullable
    public final String condition;
    @Nullable
    public final String component;
    @Nullable
    public final String callbackUrl;
    @Nullable
    public final String properties;
    @Nullable
    public final Integer xAxis;
    @Nullable
    public final Integer yAxis;

    public static AddJobDp getInstance(@NonNull AddJobRq addJobRq, @NonNull Long flowId) {
        return new AddJobDp(flowId,
                addJobRq.getName(),
                addJobRq.getCondition(),
                addJobRq.getComponent(),
                addJobRq.getCallbackUrl(),
                addJobRq.getPropertiesString(),
                addJobRq.getXAxis(),
                addJobRq.getYAxis());
    }
}
