/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.server.entity.X5Config;
import com.xiaomi.thain.server.entity.dp.X5ConfigDp;
import com.xiaomi.thain.server.entity.dr.X5ConfigDr;
import lombok.NonNull;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:39
 */
@Component
public interface X5Mapper {

    @Select("select id, app_id, app_key, create_time from thain_x5_config where app_id = #{appId}")
    Optional<X5Config> getX5Config(@NonNull @Param("appId") String appId);

    /**
     * select all x5Configs
     * @return
     */
    @Results(value = {@Result(column = "app_id",property = "appId"),@Result(column = "app_key",property = "appKey"),
    @Result(column = "app_name",property = "appName"),@Result(column = "principal",property = "principal"),
    @Result(column = "app_description",property = "description"),@Result(column = "create_time",property = "createTime")},
    id = "resultMap")
    @Select("select app_id,app_key,app_name,principal,app_description,create_time from thain_x5_config")
    List<X5ConfigDr> getAllX5Config();

    /**
     * delete a x5config
     * @param appId
     */
    @Delete("delete from thain_x5_config where app_id=#{appId}")
    void deleteX5Config(@NonNull @Param("appId")String appId);

    /**
     * add x5config
     * @param x5ConfigDp
     */
    @Insert("insert into thain_x5_config(app_id,app_key,app_name,principal,app_description)" +
            " values(#{appId},#{appKey},#{appName},#{principal},#{description}) " )
    void addOrUpdateX5Config(@NonNull X5ConfigDp x5ConfigDp);

    /**
     * update X5Config
     * @param x5ConfigDp
     */
    @Update("update thain_x5_config set app_key=#{appKey},app_name=#{appName},principal=#{principal},app_description=#{description} where app_id=#{appId} ")
    void updateX5Config(@NonNull X5ConfigDp x5ConfigDp);
}

