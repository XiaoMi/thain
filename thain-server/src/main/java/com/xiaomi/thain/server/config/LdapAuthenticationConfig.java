/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.config;

import com.xiaomi.thain.server.model.ThainUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;
import java.util.Objects;

/**
 * @author liangyongrui
 */
@Lazy
@Configuration
public class LdapAuthenticationConfig {

    private static final String UP_ATTRIBUTE_NAME = "userPassword";
    private static final String USERNAME_ATTRIBUTE_NAME = "sn";
    private static final String USER_ID_ATTRIBUTE_NAME = "uid";
    private static final String GROUP_ATTRIBUTE_NAME = "ou";

    private final LdapContextSource ldapContextSource;

    public LdapAuthenticationConfig(LdapContextSource ldapContextSource) {
        this.ldapContextSource = ldapContextSource;
    }

    @Bean("LdapAuthentication")
    public AuthenticationProvider ldapAuthenticationProvider() {
        BindAuthenticator bindAuthenticator = new BindAuthenticator(ldapContextSource);
        bindAuthenticator.setUserDnPatterns(new String[]{"uid={0},ou=people"});
        LdapAuthenticationProvider authProvider = new LdapAuthenticationProvider(bindAuthenticator);
        authProvider.setUserDetailsContextMapper(new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                                  Collection<? extends GrantedAuthority> authorities) {
                Object p = ctx.getObjectAttribute(UP_ATTRIBUTE_NAME);
                if (Objects.isNull(p)) {
                    p = "protected";
                }
                if (!(p instanceof String)) {
                    p = new String((byte[]) p);
                }
                return ThainUser.builder()
                        .userId(ctx.getStringAttribute(USER_ID_ATTRIBUTE_NAME))
                        .username(ctx.getStringAttribute(USERNAME_ATTRIBUTE_NAME))
                        .passwordHash(String.valueOf(p))
                        .admin("admin".equals(ctx.getStringAttribute(GROUP_ATTRIBUTE_NAME)))
                        .build();
            }
        });
        return authProvider;
    }
}
