package com.xiaomi.thain.server.entity.rq;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/28
 */
@Builder
public class UserRq {
    @NonNull
    public final String userId;
    public final boolean admin;
    @Nullable
    public final String email;
    @Nullable
    public final String username;
    @Nullable
    public final String password;
}
