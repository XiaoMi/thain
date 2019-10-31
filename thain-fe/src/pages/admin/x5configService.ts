/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { get, post, del, patch } from '@/utils/request';

import { ApiResult } from '@/typings/ApiResult';
import { X5ConfigModel, X5TableModel } from './models/X5ConfigModel';

export async function getClients(props: { page?: number; pageSize?: number }) {
  return get<ApiResult>('/api/admin/clients', props);
}

export async function delteClient(appId: string) {
  return del<ApiResult>(`/api/admin/client/${appId}`);
}

export async function addClient(params: X5ConfigModel) {
  return post<ApiResult>('/api/admin/client', params);
}

export async function updateClent(params: X5TableModel) {
  return patch<ApiResult>('/api/admin/client', params);
}
