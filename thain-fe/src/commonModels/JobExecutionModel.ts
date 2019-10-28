/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * 数据库中的thain_job_execution
 */
export interface JobExecutionModel {
  id: number;
  /**
   * 关联job的名称
   */
  name?: string;
  flowExecutionId: number;
  jobId: number;
  status: number;
  logs: string;
  createTime: number;
  updateTime: number;
}
