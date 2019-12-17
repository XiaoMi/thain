/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.aop;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.common.entity.X5;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.utils.X5Utils;
import com.xiaomi.thain.server.model.X5Config;
import com.xiaomi.thain.server.service.X5Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:04
 */
@Aspect
@Component
@Slf4j
public class X5Aop {

    private final LoadingCache<String, X5Config> appIdValue = Caffeine.newBuilder()
            .maximumSize(10_000)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(this::getX5Config);

    private X5Config getX5Config(String appId) {
        return x5Service.getX5Config(appId);
    }

    private final X5Service x5Service;

    public X5Aop(X5Service x5Service) {
        this.x5Service = x5Service;
    }

    @Pointcut("execution(* com.xiaomi.thain.server.controller.x5.*.*(..))")
    public void controllerPointcut() {
        // point cut
    }

    /**
     * x5协议封装, http请求携带的参数必须有"data"
     * 参数校验、权限校验
     */
    @Around("controllerPointcut()")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(requestAttributes)) {
                return ApiResult.fail("failed to obtain data parameter");
            }
            HttpServletRequest request = requestAttributes.getRequest();
            String data = request.getParameter("data");
            if (StringUtils.isEmpty(data)) {
                return ApiResult.fail("null data parameter");
            }
            X5 x5 = X5Utils.getX5(data);
            String appKey = Optional.ofNullable(appIdValue.get(x5.getHeader().getAppid()))
                    .map(X5Config::getAppKey)
                    .orElseThrow(() -> new ThainException("appId does not exist"));
            if (!X5Utils.validate(x5, appKey)) {
                return ApiResult.fail("x5 validate failed");
            }
            Object body = Optional.of(joinPoint)
                    .map(JoinPoint::getArgs)
                    .map(t -> t[0])
                    .map(Object::getClass)
                    .filter(t -> !t.equals(String.class))
                    .map(t -> (Object) JSON.parseObject(x5.getBody(), t)).orElseGet(x5::getBody);
            String appId = x5.getHeader().getAppid();
            return joinPoint.proceed(new Object[]{body, appId});
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

}

