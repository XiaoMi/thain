import { formatMessage } from 'umi-plugin-react/locale';

/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * flowExecution的执行状态
 *
 * @author liangyongrui
 */
export enum FlowExecutionStatus {
  /**
   * 0 等待运行
   */
  WAITING = 0,
  /**
   * 1 正在运行
   */
  RUNNING = 1,
  /**
   * 2 执行成功
   */
  SUCCESS = 2,
  /**
   * 3 执行异常
   */
  ERROR = 3,

  /**
   * 4 KILLED
   */
  KILLED = 4,
  /**
   * 5 禁止同时运行一个flow
   */
  DO_NOT_RUN_SAME_TIME = 5,
  /**
   * 超时自动kill
   */
  AUTO_KILLED = 6,
}
const map = {
  [FlowExecutionStatus.WAITING]: formatMessage({ id: 'flow.execution.waiting' }),
  [FlowExecutionStatus.RUNNING]: formatMessage({ id: 'flow.execution.running' }),
  [FlowExecutionStatus.SUCCESS]: formatMessage({ id: 'flow.execution.success' }),
  [FlowExecutionStatus.ERROR]: formatMessage({ id: 'flow.execution.error' }),
  [FlowExecutionStatus.KILLED]: formatMessage({ id: 'flow.execution.killed' }),
  [FlowExecutionStatus.DO_NOT_RUN_SAME_TIME]: formatMessage({
    id: 'flow.execution.do.not.run.same.time',
  }),
  [FlowExecutionStatus.AUTO_KILLED]: formatMessage({
    id: 'flow.execution.timeout.auto.killed',
  }),
};

export function getScheduleStatusDesc(enumStatus: FlowExecutionStatus) {
  return map[enumStatus];
}
