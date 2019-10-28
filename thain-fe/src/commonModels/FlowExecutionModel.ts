/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * 数据库中的 thain_flow_execution
 */
export class FlowExecutionModel {
  id = 0;
  flowId = 0;
  status = 0;
  hostInfo = '';
  triggerType = 0;
  logs = '';
  createTime = 0;
  updateTime = 0;
}
