import { formatMessage } from 'umi-plugin-react/locale';

/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * flow的调度状态
 *
 * @author liangyongrui
 */
export enum FlowSchedulingStatus {
  /**
   * 1 调度中
   */
  SCHEDULING = 1,
  /**
   * 2 暂停调度
   */
  PAUSE = 2,

  /**
   * 3 未设置调度（只运行一次的任务）
   */
  NOT_SET = 3,
}
const codeMap = new Map([
  [FlowSchedulingStatus.SCHEDULING, formatMessage({ id: 'flow.schedule.scheduling' })],
  [FlowSchedulingStatus.PAUSE, formatMessage({ id: 'flow.schedule.pause' })],
  [FlowSchedulingStatus.NOT_SET, formatMessage({ id: 'flow.schedule.not.set' })],
]);

export function getScheduleStatusDesc(enumCode: FlowSchedulingStatus) {
  return codeMap.get(enumCode);
}
const descMap = new Map([
  [formatMessage({ id: 'flow.schedule.scheduling' }), FlowSchedulingStatus.SCHEDULING],
  [formatMessage({ id: 'flow.schedule.pause' }), FlowSchedulingStatus.PAUSE],
  [formatMessage({ id: 'flow.schedule.not.set' }), FlowSchedulingStatus.NOT_SET],
]);

export function getScheduleStatusCode(enumDesc: string) {
  return descMap.get(enumDesc) || 0;
}
let entries: [string, number][];
export function FlowSchedulingStatusGetEntries() {
  if (entries === undefined) {
    entries = [];
    for (const enumMember of Object.keys(FlowSchedulingStatus)) {
      const value = parseInt(enumMember, 10);
      if (value > 0) {
        entries.push([FlowSchedulingStatus[enumMember], value]);
      }
    }
  }
  return entries;
}
