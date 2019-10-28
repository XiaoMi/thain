/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { get, patch } from '@/utils/request';
import { FlowExecutionListModelState } from '@/pages/FlowExecution/List/models/flowExecutionList';
import { FlowExecutionModel } from '@/commonModels/FlowExecutionModel';
import { FlowExecutionAllInfo } from '@/commonModels/FlowExecutionAllInfo';

export function getTableList(data: { flowId?: number; page?: number; pageSize?: number }) {
  return get<FlowExecutionListModelState>('/api/flow-execution/list', data);
}

export function killFlowExecution(flowExecutionId: number) {
  return patch<FlowExecutionModel[]>('/api/flow-execution/kill/' + flowExecutionId);
}

export function getFlowExecution(flowExecutionId: number) {
  return get<FlowExecutionModel>('/api/flow-execution/' + flowExecutionId);
}

export function getFlowExecutionAllInfoByFlowExecutionId(flowExecutionId: number) {
  return get<FlowExecutionAllInfo>('/api/flow-execution/all-info/' + flowExecutionId);
}
