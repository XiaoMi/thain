package com.xiaomi.thain.server.entity.dp;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;


/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/25
 */
@Builder
public class X5ConfigDp {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @Nullable
    public final String description;
    @NonNull
    public final String principal;
}
