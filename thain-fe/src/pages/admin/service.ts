/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { get, post, del } from '@/utils/request';
import { UserModel } from './model';
import { ApiResult, TableResult } from '@/typings/ApiResult';

export async function addUser(props: UserModel) {
  return post('api/admin/user', props);
}

export async function getUsers(props: { page?: number; pageSize?: number }) {
  return get<TableResult>('api/admin/users', props);
}

export async function deleteUser(props: { userId: string }) {
  return del<ApiResult>(`api/admin/user/${props.userId}`);
}
