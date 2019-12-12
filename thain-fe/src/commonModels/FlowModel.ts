/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * 数据库中的thain_flow
 */
export class FlowModel {
  id?: number;
  name = '';
  cron?: string;
  createUser?: string;
  callbackEmail?: string;
  callbackUrl?: string;
  createAppId?: string;
  lastRunStatus?: number;
  schedulingStatus?: number;
  public slaDuration?: number;
  public slaEmail?: string;
  public slaKill?: boolean;
  retryNumber?: number;
  retryTimeInterval?: number;
  createTime?: number;
  updateTime?: number;
  statusUpdateTime?: number;
}
