/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.config;

import com.xiaomi.thain.server.model.ThainUser;
import com.xiaomi.thain.server.service.UserService;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-8-7上午10:02
 */
@Component
public class CustomOauth2UserServiceImpl implements OAuth2UserService<OidcUserRequest, OidcUser> {

    @NonNull
    private final UserService thainUserService;

    public CustomOauth2UserServiceImpl(@NonNull UserService thainUserService) {
        this.thainUserService = thainUserService;
    }

    @Override
    public OidcUser loadUser(@NonNull OidcUserRequest userRequest) {
        Assert.notNull(userRequest, "userRequest cannot be null");
        OidcUserInfo userInfo = null;
        Set<GrantedAuthority> authorities = Collections.singleton(
                new OidcUserAuthority(userRequest.getIdToken(), userInfo));

        OidcUser user;
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            user = new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        } else {
            user = new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
        }
        //第三方登陆后插入数据库
        String userId = String.valueOf(user.getAttributes().get("email"));
        String userName = String.valueOf(user.getAttributes().get("name"));
        thainUserService.insertThirdUser(ThainUser.builder().userId(userId).username(userName).build());
        return thainUserService.getUserById(userId);
    }

}
