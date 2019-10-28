/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * flowExecution list 的model
 */

import { Effect } from 'dva';
import { Reducer } from 'redux';
import { getTableList, killFlowExecution } from '@/pages/FlowExecution/List/service';
import { notification } from 'antd';
import ConnectState from '@/models/connect';
import { FlowExecutionModel } from '@/commonModels/FlowExecutionModel';
import { formatMessage } from 'umi-plugin-react/locale';

export class FlowExecutionListModelState {
  flowId?: number;
  page = 1;
  pageSize = 20;
  count = 0;
  data: FlowExecutionModel[] = [];
}

interface FlowExecutionListModelType {
  namespace: 'flowExecutionList';
  state: FlowExecutionListModelState;
  effects: {
    fetchTable: Effect;
    killFlowExecution: Effect;
  };
  reducers: {
    updateState: Reducer<FlowExecutionListModelState>;
    unmount: Reducer<FlowExecutionListModelState>;
  };
}

const FlowExecutionListModel: FlowExecutionListModelType = {
  namespace: 'flowExecutionList',
  state: new FlowExecutionListModelState(),

  effects: {
    *fetchTable({ payload }, { call, put, select }) {
      const state: FlowExecutionListModelState = yield select(
        (s: ConnectState) => s.flowExecutionList,
      );
      let { page, pageSize, flowId } = state;
      if (payload) {
        page = payload.page;
        pageSize = payload.pageSize;
        flowId = payload.flowId;
      }
      const response: FlowExecutionListModelState | undefined = yield call(() =>
        getTableList({
          page,
          pageSize,
          flowId,
        }),
      );
      yield put({
        type: 'updateState',
        payload: { ...response, flowId },
      });
    },
    *killFlowExecution({ payload: { flowExecutionId } }, { call, put }) {
      const response = yield call(killFlowExecution, flowExecutionId);
      if (response !== undefined) {
        notification.success({
          message: formatMessage({ id: 'flow.kill.success' }),
        });
        yield put({
          type: 'fetchTable',
        });
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
      return new FlowExecutionListModelState();
    },
  },
};

export default FlowExecutionListModel;
