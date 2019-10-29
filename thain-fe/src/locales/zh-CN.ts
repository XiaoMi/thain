/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import global from './zh-CN/global';
import httpCode from './zh-CN/httpCode';
import flow from './zh-CN/flow';
import flowExecution from './zh-CN/flowExecution';
import dashboard from './zh-CN/dashboard';
import menu from './zh-CN/menu';
import userLogin from '@/pages/User/Login/locales/zh-CN';
import exception403 from '@/pages/Exception/403/locales/zh-CN';
import exception404 from '@/pages/Exception/404/locales/zh-CN';
import exception500 from '@/pages/Exception/500/locales/zh-CN';
import admin from './zh-CN/admin';
import x5config from './zh-CN/x5config';
export default {
  ...global,
  ...flow,
  ...flowExecution,
  ...dashboard,
  ...httpCode,
  ...menu,
  ...userLogin,
  ...exception403,
  ...exception404,
  ...exception500,
  ...admin,
  ...x5config,
};
