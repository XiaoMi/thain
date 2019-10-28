/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.checker;

import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.core.process.runtime.storage.FlowExecutionStorage;
import lombok.NonNull;
import lombok.val;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Date 19-5-17 下午12:38
 * 判断job的condition是否合法
 *
 * @author liangyongrui@xiaomi.com
 */
public class JobConditionChecker {

    private static final String REGEX = "^(.|\\s)*?(>|<|>=|<=|==|!=)\\s*?(\\d*)$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @NonNull
    private final FlowExecutionStorage flowExecutionStorage;

    private JobConditionChecker(final long flowExecutionId) {
        this.flowExecutionStorage = FlowExecutionStorage.getInstance(flowExecutionId);
    }

    public static JobConditionChecker getInstance(final long flowExecutionId) {
        return new JobConditionChecker(flowExecutionId);
    }

    /**
     * 判断条件是否可执行
     */
    public boolean executable(@Nullable String condition) {
        return Optional.ofNullable(condition)
                .map(t -> t.split("&&|\\|\\|"))
                .map(Arrays::stream).orElseGet(Stream::empty)
                .map(String::trim).noneMatch(t -> {
                    if (t.isEmpty()) {
                        return false;
                    }
                    val end = t.indexOf('.');
                    val name = end == -1 ? t : t.substring(0, end);
                    if (!flowExecutionStorage.finished(name)) {
                        return true;
                    }
                    if (end != -1) {
                        val predicate = t.substring(end).trim();
                        Matcher matcher = PATTERN.matcher(predicate);
                        if (matcher.find()) {
                            val left = matcher.group(1).trim();
                            val op = matcher.group(2);
                            val right = Integer.valueOf(matcher.group(3));
                            return !calculate(name, left, op, right);
                        } else {
                            return false;
                        }
                    }
                    return false;
                });
    }

    private boolean calculate(String name, String left, String op, Integer right) {
        try {
            long leftValue = Long.parseLong(String.valueOf(flowExecutionStorage.get(name, left)
                    .orElseThrow(() -> new ThainRuntimeException("The calculated value does not exist"))));
            switch (op) {
                case "==":
                    return leftValue == right;
                case "!=":
                    return leftValue != right;
                case ">":
                    return leftValue > right;
                case "<":
                    return leftValue < right;
                case ">=":
                    return leftValue >= right;
                case "<=":
                    return leftValue <= right;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
