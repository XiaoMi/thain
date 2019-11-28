/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { TableResult, ApiResult } from '@/typings/ApiResult';
import { Effect } from 'dva';
import { Reducer } from 'redux';
import { getClients, delteClient, addClient, updateClent } from '../x5configService';
import ConnectState from '@/models/connect';
import { notification } from 'antd';

export class X5ConfigModel {
  appId: string = '';
  appKey: string = '';
  appName: string = '';
  principals: Array<string> = new Array<string>();
  description: string = '';
  createTime?: number = 0;
}

export class X5TableModel {
  tableResult: TableResult<X5ConfigModel> = new TableResult<X5ConfigModel>();
}

export interface X5Model {
  namespace: string;
  state: X5TableModel;
  effects: {
    fetchTable: Effect;
    delete: Effect;
    update: Effect;
    add: Effect;
  };
  reducers: {
    updateState: Reducer<X5TableModel>;
    unmount: Reducer<X5TableModel>;
  };
}

const X5ConfigModelType: X5Model = {
  namespace: 'x5config',
  state: new X5TableModel(),
  effects: {
    *fetchTable({ payload }, { call, put }) {
      const tableResult: X5TableModel = yield call(getClients, payload);
      yield put({
        type: 'updateState',
        payload: { tableResult },
      });
    },
    *delete({ payload }, { call, put, select }) {
      yield call(delteClient, payload);
      notification.success({
        message: 'Tips',
        description: 'Delelte X5Config Success',
        duration: 1,
      });
      const state: TableResult<X5ConfigModel> = yield select(
        (s: ConnectState) => s.x5config.tableResult,
      );
      const result: ApiResult<X5ConfigModel> = yield call(getClients, {
        page: state.page,
        pageSize: state.pageSize,
      });
      yield put({
        type: 'updateState',
        payload: { tableResult: result },
      });
    },
    *update({ payload }, { call, select, put }) {
      yield call(updateClent, payload);
      notification.success({
        message: 'Tips',
        description: 'Update X5Config Success',
        duration: 1,
      });
      const state: TableResult<X5ConfigModel> = yield select(
        (s: ConnectState) => s.x5config.tableResult,
      );
      const result: ApiResult<X5ConfigModel> = yield call(getClients, {
        page: state.page,
        pageSize: state.pageSize,
      });
      yield put({
        type: 'updateState',
        payload: { tableResult: result },
      });
    },
    *add({ payload, callback }, { call, select, put }) {
      const result: ApiResult = yield call(addClient, payload);
      if (result !== undefined && callback) {
        const state: TableResult<X5ConfigModel> = yield select(
          (s: ConnectState) => s.x5config.tableResult,
        );
        notification.success({
          message: 'Tips',
          description: 'Add X5Config Success',
          duration: 1,
        });
        const newState: ApiResult<X5ConfigModel> = yield call(getClients, {
          page: state.page,
          pageSize: state.pageSize,
        });
        callback(false);
        yield put({
          type: 'updateState',
          payload: { tableResult: newState },
        });
      }
    },
  },
  reducers: {
    updateState(state, action) {
      return { ...state, ...action.payload };
    },
    unmount() {
      return new X5TableModel();
    },
  },
};

export default X5ConfigModelType;
