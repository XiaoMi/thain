/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { get } from '@/utils/request';
import { FlowExecutionStatus } from '@/enums/FlowExecutionStatus';
import { JobExecutionStatus } from '@/enums/JobExecutionStatus';

export async function getScheduleStatusCount(filterSource: string[]) {
  return get<
    {
      status: FlowSchedulingStatus;
      count: number;
    }[]
  >('/api/dashboard/schedule-status-count', { filterSource });
}

export async function getFlowSourceCount(
  filterScheduleStatus: [
    FlowSchedulingStatus.NOT_SET,
    FlowSchedulingStatus.PAUSE,
    FlowSchedulingStatus.SCHEDULING,
  ],
) {
  return get<
    {
      source: string;
      count: number;
    }[]
  >('/api/dashboard/flow-source-count', { filterScheduleStatus });
}

export async function getFlowExecutionStatusCount(period: number[]) {
  return get<
    {
      status: FlowExecutionStatus;
      count: number;
    }[]
  >('/api/dashboard/flow-execution-status-count', { period });
}

export async function getJobExecutionStatusCount(period: number[]) {
  return get<
    {
      status: JobExecutionStatus;
      count: number;
    }[]
  >('/api/dashboard/job-execution-status-count', { period });
}

export async function getRunningFlowCount(
  filterSource: string[],
  filterScheduleStatus: [
    FlowSchedulingStatus.NOT_SET,
    FlowSchedulingStatus.PAUSE,
    FlowSchedulingStatus.SCHEDULING,
  ],
) {
  return get<number>('/api/dashboard/running-flow-count', { filterSource, filterScheduleStatus });
}

export async function getRunningJobCount(
  filterSource: string[],
  filterScheduleStatus: [
    FlowSchedulingStatus.NOT_SET,
    FlowSchedulingStatus.PAUSE,
    FlowSchedulingStatus.SCHEDULING,
  ],
) {
  return get<number>('/api/dashboard/running-job-count', { filterSource, filterScheduleStatus });
}

export async function getIncreaseFlowCount(period: number[]) {
  return get<number>('/api/dashboard/increase-flow-count', { period });
}

export async function getIncreaseJobCount(period: number[]) {
  return get<number>('/api/dashboard/increase-job-count', { period });
}

export async function getStatusHistoryCount(period: number[], maxPointNum: number) {
  return get<
    {
      status: string;
      time: string;
      count: string;
    }[]
  >('/api/dashboard/status-history-count', { period, maxPointNum });
}
