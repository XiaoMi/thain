package com.xiaomi.thain.server.entity.request;

import lombok.Builder;
import lombok.NonNull;

/**
 * @author  wangsimin@xiaomi.com
 */
@Builder
public class UserRequest {
    @NonNull
    public final String userId;
    public final boolean admin;
    @NonNull
    public final String email;
    @NonNull
    public final String username;
    @NonNull
    public  final  String password;
}
