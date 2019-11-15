/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 * date 2019年03月19日
 * @author liangyongrui@xiaomi.com
 */

import Editor from '../editor';
import React, { useRef, useEffect, useState } from 'react';
import { connect } from 'dva';
import ConnectState, { ConnectProps } from '@/models/connect';
import { Modal } from 'antd';
import NodeDetail from './NodeDetail';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
  flowId: number;
}

const Page: React.FC<Props> = ({ editor, flowId, dispatch }) => {
  const pageContainer = useRef(null);
  const [jobVisible, setJobVisible] = useState(false);
  function createPage(container: any): any {
    const height = window.innerHeight - 118;
    return new Editor.Flow({
      graph: { container, height },
      align: { grid: true },
    });
  }

  useEffect(() => {
    if (editor) {
      const page = createPage(pageContainer.current);
      page.changeAddEdgeModel({ shape: 'flow-smoot' });
      page.getGraph().on('node:click', (ev: any) => {
        if (dispatch) {
          dispatch({
            type: 'flowEditor/changeSelectedModel',
            payload: {
              selectedModel: ev.item.getModel(),
            },
            callback: () => setJobVisible(true),
          });
        }
      });
      editor.add(page);
      if (dispatch && flowId) {
        dispatch({
          type: 'flowEditor/loadPage',
          payload: {
            flowId,
            page,
          },
        });
      }

      document.onclick = () => {
        // const currentPage = editor.getCurrentPage();
        // const nodes: any[] = currentPage.getNodes();
        // const edges: any[] = currentPage.getEdges();
        // const nodeMap = nodes.reduce<Map<string, any>>((res, node) => {
        //   res.set(node.id, node);
        //   return res;
        // }, new Map<string, any>());
        // edges
        //   .map(edge => edge.model)
        //   .forEach(model => {
        //     const { source, target } = model;
        //     const targetNode = nodeMap.get(target);
        //     const sourceNode = nodeMap.get(source);
        //     const targetCondition: string = targetNode.model.condition;
        //     const sourceLabel = sourceNode.model.label;
        //     if (!targetCondition) {
        //       currentPage.update(targetNode, { condition: sourceLabel });
        //       return;
        //     }
        //     if (targetCondition.indexOf(sourceLabel) === -1) {
        //       currentPage.update(targetNode, { condition: `${targetCondition} && ${sourceLabel}` });
        //     }
        // });
      };
    }
  }, [editor]);
  return (
    <div>
      <div className="page" ref={pageContainer} />
      <Modal
        width="60%"
        title={formatMessage({ id: 'flow.node.edit' })}
        visible={jobVisible}
        onOk={() => setJobVisible(false)}
        onCancel={() => setJobVisible(false)}
        footer={false}
      >
        <NodeDetail key={jobVisible + ''} />
      </Modal>
    </div>
  );
};

export default connect(({ flowEditor }: ConnectState) => ({
  ...flowEditor,
}))(Page);
