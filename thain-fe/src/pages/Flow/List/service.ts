/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { del, get, patch } from '@/utils/request';
import { TableResult } from '@/typings/ApiResult';
import { FlowModel } from '@/commonModels/FlowModel';
import { FlowSearch } from './model';

export async function getTableList(props: FlowSearch) {
  return get<TableResult<FlowModel>>('/api/flow/list', props);
}

export async function deleteFlow(flowId: number) {
  return del('/api/flow/' + flowId);
}

export async function startFlow(flowId: number) {
  return patch('/api/flow/start/' + flowId);
}

export async function schedulingFlow(flowId: number) {
  return patch('/api/flow/scheduling/' + flowId);
}

export async function pauseFlow(flowId: number) {
  return patch('/api/flow/pause/' + flowId);
}

export async function killFlow(flowId: number) {
  return patch(`/api/flow/kill/${flowId}`);
}
