/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 * @date 2019年03月19日
 * @author liangyongrui@xiaomi.com
 */
import Editor from '../editor';
import React, { useRef, useEffect } from 'react';
import '../style/itempanel.less';
import { connect } from 'dva';
import { ConnectState, ConnectProps } from '@/models/connect';
import { ComponentDefineJsons } from '@/typings/entity/ComponentDefineJsons';
import { formatMessage } from 'umi-plugin-react/locale';

const nodeCommonDefine = {
  src: require('../item-icon/node.svg'),
  'data-size': '80*48',
  'data-shape': 'flow-rect',
  'data-color': '#1890FF',
  'data-type': 'node',
  'data-attributes': '',
  draggable: false,
};
interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
  componentDefines?: ComponentDefineJsons;
}

const ItemPanel: React.FC<Props> = ({ editor, componentDefines }) => {
  if (componentDefines === undefined) {
    return <div />;
  }
  const itemPanelContainer = useRef(null);
  useEffect(() => {
    if (editor) {
      const itemPanel = new Editor.Itempanel({ container: itemPanelContainer.current });
      editor.add(itemPanel);
    }
  }, [editor]);

  const contain: any[] = [];
  for (const componentName of Object.keys(componentDefines)) {
    contain.push(
      <div
        style={{ position: 'relative' }}
        key={componentName}
        {...nodeCommonDefine}
        data-category={componentName}
        data-label=""
        className="getItem"
      >
        <img
          src={nodeCommonDefine.src}
          draggable={false}
          alt={formatMessage({ id: 'flow.node' })}
        />
        <div style={{ position: 'absolute', fontSize: '12px', left: '25px', top: '37px' }}>
          {componentName}
        </div>
      </div>,
    );
  }

  return (
    <div className="itempanel" ref={itemPanelContainer}>
      {contain}
    </div>
  );
};

export default connect(({ flowEditor }: ConnectState) => ({
  ...flowEditor,
}))(ItemPanel);
