/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * flow list 的model
 */

import { Effect } from 'dva';
import { Reducer } from 'redux';
import {
  deleteFlow,
  getTableList,
  pauseFlow,
  schedulingFlow,
  startFlow,
} from '@/pages/Flow/List/service';
import { ConnectState } from '@/models/connect';
import { TableResult } from '@/typings/ApiResult';
import { FlowModel } from '@/commonModels/FlowModel';
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { notification } from 'antd';
import { formatMessage } from 'umi-plugin-react/locale';

export interface FetchTableData {
  flowId?: number;
  page?: number;
  pageSize?: number;
  lastRunStatus?: number;
}

export class FlowListModelState {
  flowId?: number;
  lastRunStatus?: number;
  flowName?: string;
  searchApp?: string;
  createUser?: string;
  scheduleStatus?: FlowSchedulingStatus;
  updateTime?: number[];
  tableResult?: TableResult<FlowModel> = new TableResult<FlowModel>();
}

interface FlowListModelType {
  namespace: 'flowList';
  state: FlowListModelState;
  effects: {
    fetchTable: Effect;
    scheduling: Effect;
    pause: Effect;
    start: Effect;
    delete: Effect;
  };
  reducers: {
    updateState: Reducer<FlowListModelState>;
    unmount: Reducer<FlowListModelState>;
  };
}

export interface FlowSearch {
  flowId?: number;
  lastRunStatus?: number;
  flowName?: string;
  searchApp?: string;
  createUser?: string;
  scheduleStatus?: FlowSchedulingStatus;
  updateTime?: number[];
  page?: number;
  pageSize?: number;
  sortKey?: string;
  sortOrderDesc?: boolean;
}

const FlowListModel: FlowListModelType = {
  namespace: 'flowList',
  state: new FlowListModelState(),

  effects: {
    *fetchTable({ payload }, { call, put, select }) {
      const state: FlowListModelState = yield select((s: ConnectState) => s.flowList);
      const props: FlowSearch = {
        flowId: payload && payload.flowId !== undefined ? payload.flowId : state.flowId,
        lastRunStatus:
          payload && payload.lastRunStatus !== undefined
            ? payload.lastRunStatus
            : state.lastRunStatus,
        page: (payload && payload.page) || (state.tableResult ? state.tableResult.page : 1),
        pageSize:
          (payload && payload.pageSize) || (state.tableResult ? state.tableResult.pageSize : 20),
        sortKey: (payload && payload.sort && payload.sort.key) || 'id',
        sortOrderDesc: (payload && payload.sort && payload.sort.orderDesc) || false,
        flowName: payload && payload.flowName !== undefined ? payload.flowName : state.flowName,
        searchApp: payload && payload.searchApp !== undefined ? payload.searchApp : state.searchApp,
        createUser:
          payload && payload.createUser !== undefined ? payload.createUser : state.createUser,
        scheduleStatus:
          payload && payload.scheduleStatus !== undefined
            ? payload.scheduleStatus
            : state.scheduleStatus,
        updateTime:
          payload && payload.updateTime !== undefined ? payload.updateTime : state.updateTime,
      };
      const tableResult: TableResult<FlowModel> | undefined = yield call(getTableList, props);
      yield put({
        type: 'updateState',
        payload: {
          tableResult,
          flowId: props.flowId,
          lastRunStatus: props.lastRunStatus,
          flowName: props.flowName,
          scheduleStatus: props.scheduleStatus,
          updateTime: props.updateTime,
          searchApp: props.searchApp,
          createUser: props.createUser,
        },
      });
    },
    *scheduling({ payload: { id } }, { call, put }) {
      const result = yield call(schedulingFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.begin.schedule.success' })}`,
      });
      yield put({
        type: 'fetchTable',
      });
    },
    *pause({ payload: { id } }, { call, put }) {
      const result = yield call(pauseFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.pause.schedule.success' })}`,
      });
      yield put({
        type: 'fetchTable',
      });
    },
    *start({ payload: { id } }, { call, put }) {
      const result = yield call(startFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.fire.success' })}`,
      });
      yield put({
        type: 'fetchTable',
      });
    },
    *delete({ payload: { id } }, { call, put }) {
      const result = yield call(deleteFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.delete.success' })}`,
      });
      yield put({
        type: 'fetchTable',
      });
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
      return new FlowListModelState();
    },
  },
};

export default FlowListModel;
