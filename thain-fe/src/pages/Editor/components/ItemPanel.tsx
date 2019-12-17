/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 * @date 2019年03月19日
 * @author liangyongrui@xiaomi.com
 */
import Editor from '../../FlowEditor/editor';
import React, { useRef, useEffect } from 'react';
import '../style/itempanel.less';
import { useSelector } from 'dva';
import { ConnectState } from '@/models/connect';
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

const ItemPanel: React.FC<{}> = () => {
  const { editor, componentDefines } = useSelector((s: ConnectState) => s.flowEditor);

  if (!componentDefines) {
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
  componentDefines
    .filter(t => !t.hidden)
    .forEach(component => {
      const fullName = `${component.group}::${component.name}`;
      contain.push(
        <div
          style={{ position: 'relative' }}
          key={fullName}
          {...nodeCommonDefine}
          data-category={fullName}
          data-label=""
          className="getItem"
        >
          <img
            src={nodeCommonDefine.src}
            draggable={false}
            alt={formatMessage({ id: 'flow.node' })}
          />
          <div style={{ position: 'absolute', fontSize: '12px', left: '25px', top: '37px' }}>
            {fullName}
          </div>
        </div>,
      );
    });

  return (
    <div className="itempanel" ref={itemPanelContainer}>
      {contain}
    </div>
  );
};

export default ItemPanel;
