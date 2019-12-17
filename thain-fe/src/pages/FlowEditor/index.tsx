/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 *
 * date 2019年03月19日
 * @author liangyongrui@xiaomi.com
 */

import Editor from './editor';

import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { EditorNode } from '@/pages/FlowEditor/EditorNode';
import { Button, notification, Popconfirm, Spin } from 'antd';
import React, { CSSProperties, useEffect } from 'react';
import { router } from 'umi';
import { addFlow } from './service';
import Contextmenu from './components/Contextmenu';
import DetailPanel from './components/FlowDetailPanel';
import ItemPanel from '../Editor/components/ItemPanel';
import Navigator from './components/Navigator';
import Page from './components/Page';
import ToolBar from './components/Toolbar';
import './style/baseFlowEditor.less';
import editorStyle from './style/editor.less';
import { connect } from 'dva';
import { ConnectState, ConnectProps } from '@/models/connect';
import { FlowAttributes } from '@/pages/FlowEditor/model';
import { FlowModel } from '@/commonModels/FlowModel';
import { JobModel } from '@/commonModels/JobModel';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
  flowId: number;
  updateGraph: (key: string, value: string, updateAttributes?: boolean) => void;
  flowAttributes: FlowAttributes;
  loading: boolean;
}
const FlowEditor: React.FC<Props> = ({
  editor,
  dispatch,
  flowId,
  flowAttributes,
  computedMatch,
  loading,
}) => {
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'flowEditor/mount',
        payload: {
          flowId: (computedMatch && computedMatch.params.flowId) || 0,
        },
      });
    }
    return () => {
      if (dispatch) {
        dispatch({
          type: 'flowEditor/unmount',
        });
      }
      document.onclick = null;
    };
  }, []);

  function saveCondition() {
    const currentPage = editor!.getCurrentPage();
    const nodes: any[] = currentPage.getNodes();
    const edges: any[] = currentPage.getEdges();
    nodes.forEach(t => currentPage.update(t.id, { condition: '' }));
    const nodeMap = nodes.reduce<Map<string, any>>((res, node) => {
      res.set(node.id, node);
      return res;
    }, new Map<string, any>());
    edges
      .map(edge => edge.model)
      .forEach(model => {
        const { source, target } = model;
        const targetNode = nodeMap.get(target);
        const sourceNode = nodeMap.get(source);
        const targetCondition: string = targetNode.model.condition;
        const sourceLabel = sourceNode.model.label;
        if (!targetCondition) {
          currentPage.update(targetNode, { condition: sourceLabel });
          return;
        }
        if (targetCondition.indexOf(sourceLabel) === -1) {
          currentPage.update(targetNode, { condition: `${targetCondition} && ${sourceLabel}` });
        }
      });
  }

  async function save() {
    saveCondition();

    const page = editor!.getCurrentPage();
    const nodes: EditorNode[] | undefined = page.save().nodes;
    const flowModel: FlowModel = { ...flowAttributes, id: flowId };
    if (nodes === undefined) {
      notification.error({ message: formatMessage({ id: 'flow.node.empty' }) });
      return;
    }
    const jobModelList: JobModel[] = nodes.map(node => JobModel.getInstance(node));
    if (dispatch) {
      const res = await addFlow({ flowModel, jobModelList });
      if (res === undefined) {
        return;
      }
      router.push('/flow/list');
      notification.success({ message: formatMessage({ id: 'flow.create.success' }) });
    }
  }

  function cancel() {
    router.goBack();
  }

  const confirmButtonStyle: CSSProperties = {
    float: 'right',
    margin: '5px 20px 0 0',
    position: 'relative',
    zIndex: 9,
  };
  return (
    <PageHeaderWrapper
      title={formatMessage({ id: 'flow.edit' })}
      content={loading && <Spin size="large" />}
    >
      <div className={editorStyle.editor}>
        <ToolBar editor={editor} />
        <div style={confirmButtonStyle}>
          <Popconfirm
            title={formatMessage({ id: 'flow.save.confirm' })}
            onConfirm={save}
            okText={formatMessage({ id: 'flow.save' })}
            cancelText={formatMessage({ id: 'flow.save.cancel' })}
          >
            <Button type="primary">{formatMessage({ id: 'flow.save' })}</Button>
          </Popconfirm>
        </div>
        <div style={confirmButtonStyle}>
          <Button type="danger" onClick={cancel}>
            {formatMessage({ id: 'flow.cancel' })}
          </Button>
        </div>
        <div style={{ height: '42px' }} />
        <div className="bottom-container">
          <Contextmenu />
          <ItemPanel />
          <DetailPanel />
          <Navigator />
          <Page />
        </div>
      </div>
    </PageHeaderWrapper>
  );
};

export default connect(({ flowEditor, loading }: ConnectState) => ({
  ...flowEditor,
  loading: loading.models.flowEditor,
}))(FlowEditor);
