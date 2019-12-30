<!--
 Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 This source code is licensed under the Apache License Version 2.0, which
 can be found in the LICENSE file in the root directory of this source tree.
-->

# Thain

![Java CI badge](https://github.com/XiaoMi/thain/workflows/Java%20CI/badge.svg)
![Node CI badge](https://github.com/XiaoMi/thain/workflows/Node%20CI/badge.svg)

![Thain Logo](https://raw.githubusercontent.com/XiaoMi/thain/master/images/logo.png)

## Other language versions

[English](./readme.md)

## 产品简介

Thain 是小米自研的新一代分布式任务调度平台，提供定时、任务编排、分布式跑等功能。
Thain 提供了任务调度与执行的一整套解决方案，在小米集团内部使用并久经考验，具有易学习、易上手、开发高效稳定的特点。
有完善的后台管理界面，支持任务的依赖，重跑，回溯，任务执行情况查看。

## 快速上手

1. Environmental Requirements

   - os : Linux or MacOS
   - jdk >= 8
   - nodejs >= 8

1. Clone Code

   ```shell
   git clone https://github.com/XiaoMi/thain.git
   ```

1. Execute in order under the project root directory

   ```shell
   cd thain-fe
   npm install
   npm run build
   cd ..
   ./gradlew clean build
   ```

1. Run

   ```shell
   java -jar thain-server/build/libs/thain-server-1.3.0-beta.0.jar
   ```

1. 打开 [localhost:9900](http://localhost:9900) 就可以看到效果了

1. 初始账号密码：admin, admin

## Maintainers

- [liangyongrui](https://github.com/liangyongrui)
- [holiday12138](https://github.com/holiday12138)

## Contributors

- [liangyongrui](https://github.com/liangyongrui)
- [SingleMeen](https://github.com/SingleMeen)
- [zuojianguang](https://github.com/zuojianguang)
- [poplp](https://github.com/poplp)
- [holiday12138](https://github.com/holiday12138)


## 帮助我们做的更好

有任何疑问都可以在 issues 中提出，或发送邮件到：

- zuojianguang@xiaomi.com
- liangyongrui@xiaomi.com
- miaoyu3@xiaomi.com
- lupeng5@xiaomi.com

## License

[Apache License Version 2.0](LICENSE) © Xiaomi
