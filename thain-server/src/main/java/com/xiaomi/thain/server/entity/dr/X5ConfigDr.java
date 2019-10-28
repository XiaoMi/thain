package com.xiaomi.thain.server.entity.dr;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;


/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/25
 */
@Builder
public class X5ConfigDr {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @NonNull
    public final String createTime;
    @Nullable
    public final String description;
    @NonNull
    public final String principal;
}
