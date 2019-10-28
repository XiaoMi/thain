/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import request, { get } from '@/utils/request';
import { CurrentUser } from '@/models/user';
import { ApiResult } from '@/typings/ApiResult';

export async function query(): Promise<any> {
  return request('/api/users');
}

export async function queryCurrent(): Promise<CurrentUser | undefined> {
  const res: ApiResult<CurrentUser> = await request('/api/login/current-user');
  if (res.status !== 200) {
    return undefined;
  }
  return res.data;
}

export async function queryNotices(): Promise<any> {
  return request('/api/notices');
}

export async function getAccessToken(code: string): Promise<string | undefined> {
  return get('/api/login/access-token', { code });
}
