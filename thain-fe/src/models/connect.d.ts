/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { AnyAction } from 'redux';
import { MenuDataItem } from '@ant-design/pro-layout';
import { RouterTypes } from 'umi';
import { GlobalModelState } from './global';
import { DefaultSettings as SettingModelState } from '../../config/defaultSettings';
import { UserModelState } from './user';
import { EffectsCommandMap } from 'dva';
import { FlowExecutionListModelState } from '../pages/FlowExecution/List/models/flowExecutionList';
import { FlowEditorModelState } from '../pages/FlowEditor/model';
import { FlowListModelState } from '@/pages/Flow/List/model';
import { FlowExecutionDetailModelState } from '@/pages/FlowExecution/List/models/flowExecutionDetail';
import { DashboardState } from '@/pages/Dashboard/model';
import { AdminUserModel } from '@/pages/admin/models/UserAdminModel';
import { X5TableModel } from '@/pages/admin/models/X5ConfigModel';
export { GlobalModelState, SettingModelState, UserModelState };

export type Effect = (
  action: AnyAction,
  effects: EffectsCommandMap & { select: <T>(func: (state: ConnectState) => T) => T },
) => void;

/**
 * @type P: Type of payload
 * @type C: Type of callback
 */
export type Dispatch = <P = any, C = (payload: P) => void>(action: {
  type: string;
  payload?: P;
  callback?: C;
  [key: string]: any;
}) => any;

export interface Loading {
  global: boolean;
  effects: { [key: string]: boolean | undefined };
  models: {
    global?: boolean;
    menu?: boolean;
    setting?: boolean;
    user?: boolean;
    flowExecutionList?: boolean;
    flowExecutionDetail?: boolean;
    flowEditor?: boolean;
    flowList?: boolean;
    admin?: boolean;
    x5config?: boolean;
  };
}

/**
 * key 为models下面的namespace
 */
export interface ConnectState {
  global: GlobalModelState;
  loading: Loading;
  settings: SettingModelState;
  user: UserModelState;
  flowExecutionList: FlowExecutionListModelState;
  flowExecutionDetail: FlowExecutionDetailModelState;
  flowList: FlowListModelState;
  flowEditor: FlowEditorModelState;
  dashboard: DashboardState;
  admin: AdminUserModel;
  x5config: X5TableModel;
}

export interface Route extends MenuDataItem {
  routes?: Route[];
}

/**
 * @type T: Params matched in dynamic routing
 */
export interface ConnectProps<T = {}> extends Partial<RouterTypes<Route, T>> {
  dispatch?<K = any>(action: AnyAction): K;
}
