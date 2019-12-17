/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { get, post } from '@/utils/request';
import { FlowAllInfo } from '@/commonModels/FlowAllInfo';
import { ComponentDefine } from '../Editor/ComponentDefine';

/**
 * 获取组件定义json
 */
export async function getComponentDefines(): Promise<ComponentDefine[] | undefined> {
  return await get<ComponentDefine[]>('/api/flow/getComponentDefineJson');
}

/**
 * 添加flow
 */
export async function addFlow(data: FlowAllInfo) {
  return post('/api/editor', JSON.stringify(data));
}

export async function getFlow(flowId: number) {
  return get<FlowAllInfo>('/api/flow/all-info/' + flowId);
}
