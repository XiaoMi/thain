package com.xiaomi.thain.server.entity.dr;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.sql.Timestamp;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/29
 */
@AllArgsConstructor
public class X5ConfigDr {
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
    @NonNull
    public final Timestamp createTime;
}
