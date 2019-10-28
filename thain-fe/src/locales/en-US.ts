/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import global from './en-US/global';
import httpCode from './en-US/httpCode';
import flow from './en-US/flow';
import flowExecution from './en-US/flowExecution';
import dashboard from './en-US/dashboard';
import menu from './en-US/menu';
import userLogin from '@/pages/User/Login/locales/en-US';
import exception403 from '@/pages/Exception/403/locales/en-US';
import exception404 from '@/pages/Exception/404/locales/en-US';
import exception500 from '@/pages/Exception/500/locales/en-US';
import admin from './en-US/admin';
import x5config from './en-US/x5config';
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
