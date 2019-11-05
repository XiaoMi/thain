package com.xiaomi.thain.server.model.dr;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;

/**
 * @author liangyongrui
 */
@AllArgsConstructor
public class JobDr {
    public final long id;
    public final long flowId;
    @NonNull
    public final String name;
    @NonNull
    public final String condition;
    @NonNull
    public final String component;
    @NonNull
    public final String callbackUrl;
    @NonNull
    public final String properties;
    public final int xAxis;
    public final int yAxis;
    @NonNull
    public final Timestamp createTime;
    public final boolean deleted;
}
