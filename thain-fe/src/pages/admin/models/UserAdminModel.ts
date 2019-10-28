/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { Effect } from 'dva';
import { ApiResult, TableResult } from '@/typings/ApiResult';
import { Reducer } from 'redux';
import { addUser, deleteUser, getUsers, updateUser } from '../adminService';
import ConnectState from '@/models/connect';
import { notification } from 'antd';

export class UserModel {
  userId?: string;
  username?: string;
  email?: string;
  admin?: boolean;
  password?: string;
}

export class AdminUserModel {
  tableResult = new TableResult<UserModel>();
}

interface AdminModel {
  namespace: string;
  state: AdminUserModel;
  reducers: {
    updateState: Reducer<AdminUserModel>;
    unmount: Reducer<AdminUserModel>;
  };
  effects: {
    delete: Effect;
    add: Effect;
    fetchTable: Effect;
    update: Effect;
  };
}

const AdminUserModelType: AdminModel = {
  namespace: 'admin',
  state: new AdminUserModel(),
  effects: {
    *add({ payload, callBack }, { call, select, put }) {
      const result: ApiResult = yield call(addUser, payload);
      if (result !== undefined && callBack) {
        notification.success({
          message: 'Tips',
          description: 'Add User Success',
          duration: 1,
        });
        callBack();
      }
      const model: TableResult<UserModel> = yield select((s: ConnectState) => s.admin.tableResult);
      const callbackModel: TableResult<UserModel> = yield call(getUsers, {
        page: model.page,
        pageSize: model.pageSize,
      });
      yield put({
        type: 'updateState',
        payload: { ...callbackModel },
      });
    },
    *fetchTable({ payload }, { call, put }) {
      const tableResult: AdminUserModel = yield call(getUsers, payload);
      yield put({
        type: 'updateState',
        payload: { tableResult },
      });
    },
    *delete({ payload }, { call, put, select }) {
      yield call(deleteUser, payload);
      const model: AdminUserModel = yield select((s: ConnectState) => s.admin);
      const {
        tableResult: { page, pageSize },
      } = model;
      const tableResult: AdminUserModel = yield call(getUsers, { page: page, pageSize: pageSize });
      yield put({
        type: 'updateState',
        payload: { tableResult },
      });
    },
    *update({ payload, callback }, { call, select, put }) {
      yield call(updateUser, payload);
      callback();
      const model: AdminUserModel = yield select((s: ConnectState) => s.admin);
      const updateModel: AdminUserModel = yield call(getUsers, {
        page: model.tableResult.page,
        pageSize: model.tableResult.pageSize,
      });
      notification.success({
        message: 'Tips',
        description: 'Update User Success',
        duration: 1,
      });
      yield put({
        type: 'updateState',
        payload: { tableResult: updateModel },
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
      return new AdminUserModel();
    },
  },
};

export default AdminUserModelType;
