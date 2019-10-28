/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * dashboard的model
 */

import { Effect } from 'dva';
import { Reducer } from 'redux';
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { FlowExecutionStatus } from '@/enums/FlowExecutionStatus';
import { JobExecutionStatus } from '@/enums/JobExecutionStatus';
import ConnectState from '@/models/connect';
import {
  getScheduleStatusCount,
  getFlowSourceCount,
  getFlowExecutionStatusCount,
  getRunningFlowCount,
  getIncreaseFlowCount,
  getIncreaseJobCount,
  getRunningJobCount,
  getStatusHistoryCount,
  getJobExecutionStatusCount,
} from './service';
import moment from 'moment';

export class DashboardState {
  scheduleStatusCount?: {
    status: FlowSchedulingStatus;
    count: number;
  }[];
  flowSourceCount?: {
    source: string;
    count: number;
  }[];
  flowExecutionStatusCount?: {
    status: FlowExecutionStatus;
    count: number;
  }[];
  jobExecutionStatusCount?: {
    status: JobExecutionStatus;
    count: number;
  }[];
  runningFlowCount?: number;
  runningJobCount?: number;
  increaseFlowCount?: number;
  increaseJobCount?: number;
  statusHistoryCount?: {
    status: number;
    time: string;
    count: number;
  }[];
  scheduleStatusLoading = true;
  flowSourceCountLoading = true;
  flowExecutionStatusCountLoading = true;
  jobExecutionStatusCountLoading = true;
  runningJobCountLoading = true;
  runningFlowCountLoading = true;
  increaseFlowCountLoading = true;
  increaseJobCountLoading = true;
  statusHistoryCountLoading = true;
  firstHistoryPeriod = [
    moment()
      .add({ day: -1 })
      .unix(),
    moment().unix(),
  ];
  secondHistoryPeriod = [
    moment()
      .add({ day: -1 })
      .unix(),
    moment().unix(),
  ];
  maxPointNum: number = 10;
  filterScheduleStatus = [];
  filterSource = [];
}

interface DashboardModelType {
  namespace: 'dashboard';
  state: DashboardState;
  effects: {
    fetchScheduleStatusCount: Effect;
    fetchFlowSourceCount: Effect;
    fetchFlowExecutionStatusCount: Effect;
    fetchJobExecutionStatusCount: Effect;
    fetchRunningJobCount: Effect;
    fetchRunningFlowCount: Effect;
    fetchIncreaseFlowCount: Effect;
    fetchIncreaseJobCount: Effect;
    fetchStatusHistoryCount: Effect;
  };
  reducers: {
    updateState: Reducer<DashboardState>;
    unmount: Reducer<DashboardState>;
  };
}

function* loadingWrapper(loadingAttr: string, put: any) {
  yield put({
    type: 'updateState',
    payload: { [loadingAttr]: true },
  });
  yield put({
    type: 'updateState',
    payload: { [loadingAttr]: false },
  });
}

const DashboardModel: DashboardModelType = {
  namespace: 'dashboard',
  state: new DashboardState(),

  effects: {
    *fetchScheduleStatusCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('scheduleStatusLoading', put);
      yield g.next().value;
      const scheduleStatusCount: {
        status: FlowSchedulingStatus;
        count: number;
      }[] = yield call(getScheduleStatusCount, state.filterSource);
      if (scheduleStatusCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { scheduleStatusCount },
        });
        yield g.next().value;
      }
    },
    *fetchFlowSourceCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('flowSourceCountLoading', put);
      yield g.next().value;
      const flowSourceCount: {
        source: string;
        count: number;
      }[] = yield call(getFlowSourceCount, state.filterScheduleStatus);
      if (flowSourceCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { flowSourceCount },
        });
        yield g.next().value;
      }
    },
    *fetchFlowExecutionStatusCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('flowExecutionStatusCountLoading', put);
      yield g.next().value;
      const flowExecutionStatusCount: {
        status: FlowExecutionStatus;
        count: number;
      }[] = yield call(getFlowExecutionStatusCount, state.firstHistoryPeriod);
      if (flowExecutionStatusCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { flowExecutionStatusCount },
        });
        yield g.next().value;
      }
    },
    *fetchJobExecutionStatusCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('jobExecutionStatusCountLoading', put);
      yield g.next().value;
      const jobExecutionStatusCount: {
        status: JobExecutionStatus;
        count: number;
      }[] = yield call(getJobExecutionStatusCount, state.firstHistoryPeriod);
      if (jobExecutionStatusCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { jobExecutionStatusCount },
        });
        yield g.next().value;
      }
    },

    *fetchRunningFlowCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('runningFlowCountLoading', put);
      yield g.next().value;
      const runningFlowCount: number = yield call(
        getRunningFlowCount,
        state.filterSource,
        state.filterScheduleStatus,
      );
      if (runningFlowCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { runningFlowCount },
        });
        yield g.next().value;
      }
    },
    *fetchRunningJobCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('runningJobCountLoading', put);
      yield g.next().value;
      const runningJobCount: number = yield call(
        getRunningJobCount,
        state.filterSource,
        state.filterScheduleStatus,
      );
      if (runningJobCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { runningJobCount },
        });
        yield g.next().value;
      }
    },

    *fetchIncreaseFlowCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('increaseFlowCountLoading', put);
      yield g.next().value;
      const increaseFlowCount: number = yield call(getIncreaseFlowCount, state.firstHistoryPeriod);
      if (increaseFlowCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { increaseFlowCount },
        });
        yield g.next().value;
      }
    },

    *fetchIncreaseJobCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('increaseJobCountLoading', put);
      yield g.next().value;
      const increaseJobCount: number = yield call(getIncreaseJobCount, state.firstHistoryPeriod);
      if (increaseJobCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { increaseJobCount },
        });
        yield g.next().value;
      }
    },

    *fetchStatusHistoryCount(_, { call, put, select }) {
      const state: DashboardState = yield select((s: ConnectState) => s.dashboard);
      const g = loadingWrapper('statusHistoryCountLoading', put);
      yield g.next().value;
      const statusHistoryCount: {
        status: string;
        time: string;
        count: string;
      }[] = yield call(getStatusHistoryCount, state.secondHistoryPeriod, state.maxPointNum);
      if (statusHistoryCount !== undefined) {
        yield put({
          type: 'updateState',
          payload: { statusHistoryCount },
        });
        yield g.next().value;
      }
    },
  },
  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
    unmount() {
      return new DashboardState();
    },
  },
};

export default DashboardModel;
