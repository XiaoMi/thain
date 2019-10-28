/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * @date 2019年03月19日
 * @author liangyongrui@xiaomi.com
 */
import React, { useRef, useEffect } from 'react';
import Editor from '../editor';
import '../style/contextmenu.less';
import { connect } from 'dva';
import { ConnectState, ConnectProps } from '@/models/connect';

interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
}

const Contextmenu: React.FC<Props> = ({ editor }) => {
  const contextmenuContainer = useRef(null);

  useEffect(() => {
    if (editor) {
      const contextmenu = new Editor.Contextmenu({ container: contextmenuContainer.current });
      editor.add(contextmenu);
    }
  }, [editor]);

  return (
    <div className="contextmenu" ref={contextmenuContainer}>
      <div data-status="node-selected" className="menu">
        <div data-command="copy" className="command">
          <span>复制</span>
          <span>copy</span>
        </div>
        <div data-command="delete" className="command">
          <span>删除</span>
          <span>delete</span>
        </div>
      </div>
      <div data-status="edge-selected" className="menu">
        <div data-command="delete" className="command">
          <span>删除</span>
          <span>delete</span>
        </div>
      </div>
      <div data-status="group-selected" className="menu">
        <div data-command="copy" className="command">
          <span>复制</span>
          <span>copy</span>
        </div>
        <div data-command="delete" className="command">
          <span>删除</span>
          <span>delete</span>
        </div>
        <div data-command="unGroup" className="command">
          <span>解组</span>
          <span>unGroup</span>
        </div>
      </div>
      <div data-status="canvas-selected" className="menu">
        <div data-command="undo" className="command">
          <span>撤销</span>
          <span>undo</span>
        </div>
        <div data-command="redo" className="command">
          <span>重做</span>
          <span>redo</span>
        </div>
        <div data-command="pasteHere" className="command">
          <span>粘贴</span>
          <span>pasteHere</span>
        </div>
      </div>
      <div data-status="multi-selected" className="menu">
        <div data-command="copy" className="command">
          <span>复制</span>
          <span>copy</span>
        </div>
        <div data-command="paste" className="command">
          <span>粘贴</span>
          <span>paste</span>
        </div>
        <div data-command="addGroup" className="command">
          <span>归组</span>
          <span>addGroup</span>
        </div>
        <div data-command="delete" className="command">
          <span>删除</span>
          <span>delete</span>
        </div>
      </div>
    </div>
  );
};

export default connect(({ flowEditor }: ConnectState) => ({
  ...flowEditor,
}))(Contextmenu);
