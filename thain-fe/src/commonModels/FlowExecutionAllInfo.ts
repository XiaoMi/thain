/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { JobModel } from './JobModel';
import { FlowExecutionModel } from './FlowExecutionModel';
import { JobExecutionModel } from './JobExecutionModel';

export class FlowExecutionAllInfo {
  flowExecutionModel: FlowExecutionModel = new FlowExecutionModel();
  jobModelList: JobModel[] = [];
  jobExecutionModelList: JobExecutionModel[] = [];
}
