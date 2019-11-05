/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.config;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.entity.ApiResult;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author miaoyu
 */

@EnableWebSecurity
@Log4j2
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2UserService<OidcUserRequest, OidcUser> customOauth2UserServiceImpl;
    private final AuthenticationProvider authenticationProvider;
    private static final String UTF8_JSON_TYPE = "application/json; charset=utf-8";

    public WebSecurityConfig(@NonNull OAuth2UserService<OidcUserRequest, OidcUser> customOauth2UserServiceImpl,
                             @NonNull @Value("${thain.login.source}") String authenticationProviderName,
                             @NonNull ApplicationContext applicationContext
    ) {
        this.customOauth2UserServiceImpl = customOauth2UserServiceImpl;
        this.authenticationProvider = (AuthenticationProvider) applicationContext.getBean(authenticationProviderName);
    }

    @Override
    protected void configure(@NonNull AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    public AuthenticationFailureHandler apiAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setContentType(UTF8_JSON_TYPE);
            response.getWriter().write(JSON.toJSONString(ApiResult.builder()
                    .status(400)
                    .message(exception.getMessage()).build()));
        };
    }

    public AuthenticationSuccessHandler apiAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType(UTF8_JSON_TYPE);
            response.getWriter().write(JSON.toJSONString(ApiResult.success(authentication.getAuthorities())));
        };
    }

    public AuthenticationEntryPoint apiAuthenticationEntryPoint(String loginPage) {
        return new LoginUrlAuthenticationEntryPoint(loginPage) {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response,
                                 AuthenticationException authException) throws IOException {
                response.setContentType(UTF8_JSON_TYPE);
                response.getWriter().write(JSON.toJSONString(ApiResult.builder().status(401).message("Logon expires, please refresh the page and log in again").build()));
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(apiAuthenticationEntryPoint("/"))
                .and()
                .authorizeRequests()
                .antMatchers("/api/login", "x5/**", "/api/cas/redirect/login").permitAll()
                .antMatchers("/api/**")
                .authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .loginProcessingUrl("/api/login")
                .failureHandler(apiAuthenticationFailureHandler())
                .successHandler(apiAuthenticationSuccessHandler())
                .usernameParameter("userId").and();
        thirdConfig(http);
    }

    /**
     * 第三方登录配置
     *
     * @param http HttpSecurity
     */
    private void thirdConfig(HttpSecurity http) throws Exception {
        http.oauth2Login()
                .loginPage("/")
                .defaultSuccessUrl("/", true)
                .authorizationEndpoint()
                .baseUri("/api/oauth2/authorization")
                .and().userInfoEndpoint().oidcUserService(customOauth2UserServiceImpl)
                .and().and()
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false);
    }
}
