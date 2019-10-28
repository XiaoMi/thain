/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.server.entity.X5Config;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:39
 */
@Component
public interface X5Mapper {

    @Select("select id, app_id, app_key, create_time from thain_x5_config where app_id = #{appId}")
    X5Config getX5Config(@NonNull @Param("appId") String appId);

}

