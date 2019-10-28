/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { EditorNode } from '@/pages/FlowEditor/EditorNode';

/**
 * 数据库中的thain_job
 */
export class JobModel {
  static getInstance(node: EditorNode): any {
    const instance = new JobModel();
    instance.name = node.label;
    instance.condition = node.condition;
    instance.component = node.category;
    instance.callbackUrl = node.callbackUrl;
    if (node.attributes) {
      instance.properties = node.attributes;
    }
    instance.xAxis = Math.floor(node.x);
    instance.yAxis = Math.floor(node.y);
    return instance;
  }
  id?: number;
  flowId?: number;
  name = '';
  condition = '';
  component = '';
  callbackUrl = '';
  properties: { [props: string]: string } = {};
  xAxis = 0;
  yAxis = 0;
  createTime?: number;
}
