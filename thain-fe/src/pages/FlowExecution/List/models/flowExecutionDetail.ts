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
import { EditorFlowEntity } from '@/pages/FlowEditor/EditorFlowEntity';
import { JobExecutionStatus } from '@/enums/JobExecutionStatus';
import { getFlowExecutionAllInfoByFlowExecutionId } from '../service';
import FlowExecutionAllInfo from '@/commonModels/FlowExecutionAllInfo';

export class FlowExecutionDetailModelState extends FlowExecutionAllInfo {}

interface FlowExecutionDetailModelType {
  namespace: 'flowExecutionDetail';
  state: FlowExecutionDetailModelState;
  effects: {
    getGraph: Effect;
  };
  reducers: {
    updateState: Reducer<FlowExecutionDetailModelState>;
    unmount: Reducer<FlowExecutionDetailModelState>;
  };
}

const FlowExecutionDetailModel: FlowExecutionDetailModelType = {
  namespace: 'flowExecutionDetail',
  state: new FlowExecutionDetailModelState(),

  effects: {
    *getGraph({ payload: { flowExecutionId, graph } }, { call, put }) {
      const flowExecutionAllInfo: FlowExecutionAllInfo | undefined = yield call(
        getFlowExecutionAllInfoByFlowExecutionId,
        flowExecutionId,
      );
      if (flowExecutionAllInfo === undefined) {
        return;
      }
      const { jobModelList, jobExecutionModelList } = flowExecutionAllInfo;
      const editorFlowEntity: EditorFlowEntity = EditorFlowEntity.getInstance({ jobModelList });
      if (editorFlowEntity === undefined) {
        return;
      }
      const getJobExecutionModel = (jobId?: number) => {
        if (jobId && jobExecutionModelList) {
          return jobExecutionModelList.find(t => t.jobId === jobId);
        }
        return undefined;
      };
      if (editorFlowEntity === undefined) {
        return;
      }
      let nodes = editorFlowEntity.editorNodes;
      const edges = editorFlowEntity.editorEdges;
      let minX = Number.MAX_VALUE;
      let minY = Number.MAX_VALUE;

      nodes.forEach(node => {
        minX = Math.min(minX, node.x);
        minY = Math.min(minY, node.y);
      });

      // 修复边框问题
      minX -= 45;
      minY -= 30;

      nodes = nodes.map(p => {
        const node = p;
        node.x -= minX;
        node.y -= minY;
        const jobExecutionModel = getJobExecutionModel(node.jobId);
        if (jobExecutionModel) {
          jobExecutionModel.name = node.label;
          node.logs = jobExecutionModel.logs;
          switch (JobExecutionStatus[jobExecutionModel.status]) {
            case 'SUCCESS':
              node.color = '#1890ff';
              break;
            case 'RUNNING':
              node.color = '#FF9933';
              break;
            case 'ERROR':
              node.color = '#FF0000';
              break;
            default:
              node.color = '#C0C0C0';
          }
        } else {
          node.color = '#C0C0C0';
        }
        return node;
      });
      graph.read({ nodes, edges });

      yield put({
        type: 'updateState',
        payload: flowExecutionAllInfo,
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
      return new FlowExecutionDetailModelState();
    },
  },
};

export default FlowExecutionDetailModel;
