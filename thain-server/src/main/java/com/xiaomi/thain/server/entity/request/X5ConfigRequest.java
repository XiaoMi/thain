package com.xiaomi.thain.server.entity.request;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/24
 */
@Builder
public class X5ConfigRequest {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @NonNull
    public final List<String> principals;
    @Nullable
    public final String description;
}
