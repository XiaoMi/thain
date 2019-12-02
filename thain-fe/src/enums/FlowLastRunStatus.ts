/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
export enum FlowLastRunStatus {
  /**
   * 1 未运行
   */
  NEVER = 1,
  /**
   * 2 运行成功
   */
  SUCCESS = 2,
  /**
   * 3 运行异常
   */
  ERROR = 3,
  /**
   * 4 正在运行
   */
  RUNNING = 4,
  /**
   * 5 手动杀死
   */
  KILLED = 5,
  /**
   * 6 暂停运行
   */
  PAUSE = 6,
  /**
   * 7 自动kill
   */
  AUTO_KILLED = 7,
}
let entries: [string, number][];
export function FlowLastRunStatusGetEntries() {
  if (entries === undefined) {
    entries = [];
    for (const enumMember of Object.keys(FlowLastRunStatus)) {
      const value = parseInt(enumMember, 10);
      if (value > 0) {
        entries.push([FlowLastRunStatus[enumMember], value]);
      }
    }
  }
  return entries;
}
