/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
export default [
  // user
  {
    path: '/user',
    component: '../layouts/UserLayout',
    routes: [
      { path: '/user', redirect: '/user/login' },
      { path: '/user/login', name: 'login', component: './User/Login' },
      {
        component: './Exception/404',
      },
    ],
  },
  // app
  {
    path: '/',
    component: '../layouts/BasicLayout',
    Routes: ['src/pages/Authorized'],
    authority: ['admin', 'user'],
    routes: [
      { path: '/', redirect: '/flow/list' },
      {
        path: '/dashboard',
        name: 'dashboard',
        icon: 'dashboard',
        component: './Dashboard',
        authority: ['admin'],
      },
      {
        path: '/flow-editor',
        name: 'editor',
        icon: 'edit',
        component: './FlowEditor',
      },
      {
        path: '/flow-editor/:flowId',
        component: './FlowEditor',
        hideInMenu: true,
      },
      {
        path: '/flow/list',
        icon: 'table',
        name: 'flows',
        component: './Flow/List',
      },
      {
        path: '/flow-execution/list',
        icon: 'profile',
        name: 'executions',
        component: './FlowExecution/List',
      },
      {
        path: '/flow-execution/list/:flowId',
        component: './FlowExecution/List',
        hideInMenu: true,
      },
      {
        path: '/admin',
        icon: 'table',
        name: 'admin',
        component: './admin',
        authority: ['admin'],
      },
      {
        hideInMenu: true,
        name: 'exception',
        icon: 'warning',
        path: '/exception',
        routes: [
          // exception
          {
            path: '/exception/403',
            name: 'not-permission',
            component: './Exception/403',
          },
          {
            path: '/exception/404',
            name: 'not-find',
            component: './Exception/404',
          },
          {
            path: '/exception/500',
            name: 'server-error',
            component: './Exception/500',
          },
        ],
      },
      {
        component: './Exception/404',
      },
    ],
  },
];
