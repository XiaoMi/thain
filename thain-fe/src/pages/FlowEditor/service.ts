/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { ComponentDefineJsons } from '@/typings/entity/ComponentDefineJsons';
import { get, post } from '@/utils/request';
import { FlowAllInfo } from '@/commonModels/FlowAllInfo';

/**
 * 获取组件定义json
 */
export async function getComponentDefineJson(): Promise<ComponentDefineJsons | undefined> {
  // todo 改了 list 了
  const res = await get<string[]>('/api/flow/getComponentDefineJson');
  if (res === undefined) {
    return undefined;
  }
  const result: ComponentDefineJsons = {};
  for (const componentName of Object.keys(res)) {
    result[componentName] = JSON.parse(res[componentName]);
  }
  return result;
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
