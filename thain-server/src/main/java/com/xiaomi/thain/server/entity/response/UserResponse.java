package com.xiaomi.thain.server.entity.response;

import lombok.*;


/**
 * @author  wangsimin3@xiaomi.com
 */

@Builder
public class UserResponse   {
   @NonNull
   public final String userId;
   public final boolean admin;
   public final String email;
   public final String userName;
}
