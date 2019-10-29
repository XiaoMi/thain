package com.xiaomi.thain.server.entity.rq;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/29
 */
@Builder
public class X5ConfigRq {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @NonNull
    public final String principal;
    @Nullable
    public final String description;
}
