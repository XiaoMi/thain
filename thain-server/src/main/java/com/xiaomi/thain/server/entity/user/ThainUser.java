/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.entity.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author miaoyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThainUser implements UserDetails, OidcUser {

    @NonNull
    private String userId;
    @NonNull
    private String username;
    @Nullable
    private String passwordHash;

    private boolean admin;
    @Nullable
    private Set<String> appIds;

    @Nullable
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        val result = new HashSet<GrantedAuthority>();
        if (admin) {
            result.add((GrantedAuthority) () -> "admin");
        }
        if (appIds != null) {
            appIds.stream().map(SimpleGrantedAuthority::new).forEach(result::add);
        }
        return result;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}
