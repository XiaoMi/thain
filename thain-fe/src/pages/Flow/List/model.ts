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
  killFlow,
} from '@/pages/Flow/List/service';
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
  tableResult: TableResult<FlowModel> = new TableResult<FlowModel>();
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
    kill: Effect;
  };
  reducers: {
    updateState: Reducer<FlowListModelState>;
    unmount: Reducer<FlowListModelState>;
  };
}

export class FlowSearch {
  flowId: number | undefined;

  lastRunStatus: number | undefined;

  flowName: string | undefined;

  searchApp: string | undefined;

  createUser: string | undefined;

  scheduleStatus: FlowSchedulingStatus | undefined;

  updateTime: number[] = [];

  page: number | undefined;

  pageSize: number | undefined;

  sortKey: string | undefined;

  sortOrderDesc: boolean | undefined;
}

const FlowListModel: FlowListModelType = {
  namespace: 'flowList',
  state: new FlowListModelState(),

  effects: {
    *fetchTable({ payload }, { call, put }) {
      const tableResult: TableResult<FlowModel> | undefined = yield call(getTableList, payload);
      yield put({
        type: 'updateState',
        payload: {
          tableResult,
        },
      });
    },
    *scheduling({ payload: { id, condition } }, { call, put }) {
      const result = yield call(schedulingFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.begin.schedule.success' })}`,
      });
      yield put({
        type: 'fetchTable',
        payload: condition,
      });
    },
    *pause({ payload: { id, condition } }, { call, put }) {
      const result = yield call(pauseFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.pause.schedule.success' })}`,
      });
      yield put({
        type: 'fetchTable',
        payload: condition,
      });
    },
    *start({ payload: { id, condition, variables } }, { call, put }) {
      const result = yield call(startFlow, id, variables);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.fire.success' })}`,
      });
      yield put({
        type: 'fetchTable',
        payload: condition,
      });
    },
    *delete({ payload: { id, condition } }, { call, put }) {
      const result = yield call(deleteFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.delete.success' })}`,
      });
      yield put({
        type: 'fetchTable',
        payload: condition,
      });
    },
    *kill({ payload: { id, condition } }, { call, put }) {
      const result = yield call(killFlow, id);
      if (result === undefined) {
        return;
      }
      notification.success({
        message: `${id}:${formatMessage({ id: 'flow.kill.success' })}`,
      });
      yield put({
        type: 'fetchTable',
        payload: condition,
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
