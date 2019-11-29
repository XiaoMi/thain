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

[简体中文](./readme_zh.md)

## Introduction

Thain is a distributed flow schedule platform, it was invented at XiaoMi Technology and the project has been proven easily-learning, developed-rapidly, stable in the XiaoMi internal departments.
Thain has completed backend management system which supports depending, re-run, roll-back and monitoring of the flows, contains bautiful and easily-manipulating web UI for users to maintain your jobs.

## Quick Start

1. Clone Code

   ```shell
   git clone https://github.com/XiaoMi/thain.git
   ```

1. Environmental Requirements

   - jdk >= 8
   - maven 3
   - nodejs >= 8

1. Execute in order under the project root directory

   ```shell
   cd thain-fe
   npm install
   npm run build
   cd ..
   mvn -U clean package
   ```

1. Run

   ```shell
   java -jar thain-server/target/thain-server-1.2.1.jar
   ```

1. Open [localhost:9900](http://localhost:9900) to see the effect

1. Initial account password: admin, admin

## Maintainers

- [liangyongrui](https://github.com/liangyongrui)
- [holiday12138](https://github.com/holiday12138)

## Contributors

- [liangyongrui](https://github.com/liangyongrui)
- [SingleMeen](https://github.com/SingleMeen)
- [zuojianguang](https://github.com/zuojianguang)
- [poplp](https://github.com/poplp)
- [holiday12138](https://github.com/holiday12138)

## WeChat user group

![WeChat](http://cdn.cnbj1.fds.api.mi-img.com/thain/WechatIMG.png)

## Help Us Do Better

Now Thain continues to be developed by xiaomi internal developers. If you have any questions, you can ask in the issues or send email to:

- zuojianguang@xiaomi.com
- liangyongrui@xiaomi.com
- miaoyu3@xiaomi.com
- lupeng5@xiaomi.com

## License

[Apache License Version 2.0](LICENSE) © Xiaomi
