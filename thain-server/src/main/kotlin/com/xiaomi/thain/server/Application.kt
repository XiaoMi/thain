/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * @author liangyongrui
 */
@MapperScan("com.xiaomi.thain.server.mapper")
//@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = ["com.xiaomi.thain"])
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
