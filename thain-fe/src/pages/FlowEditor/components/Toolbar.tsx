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
import '../style/toolbar.less';
import { connect } from 'dva';
import ConnectState, { ConnectProps } from '@/models/connect';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
}
const Toolbar: React.FC<Props> = ({ editor }) => {
  const toolbarContainer = useRef(null);

  useEffect(() => {
    if (editor) {
      const toolbar = new Editor.Toolbar({ container: toolbarContainer.current });
      editor.add(toolbar);
    }
  }, [editor]);

  return (
    <div className="toolbar" ref={toolbarContainer}>
      <i
        data-command="undo"
        className="command iconfont icon-undo"
        title={formatMessage({ id: 'global.undo' })}
      />
      <i
        data-command="redo"
        className="command iconfont icon-redo"
        title={formatMessage({ id: 'global.redo' })}
      />
      <span className="separator" />
      <i
        data-command="copy"
        className="command iconfont icon-copy-o"
        title={formatMessage({ id: 'global.copy' })}
      />
      <i
        data-command="paste"
        className="command iconfont icon-paster-o"
        title={formatMessage({ id: 'global.paste' })}
      />
      <i
        data-command="delete"
        className="command iconfont icon-delete-o"
        title={formatMessage({ id: 'global.delete' })}
      />
      <span className="separator" />
      <i
        data-command="zoomIn"
        className="command iconfont icon-zoom-in-o"
        title={formatMessage({ id: 'global.zoom.in' })}
      />
      <i
        data-command="zoomOut"
        className="command iconfont icon-zoom-out-o"
        title={formatMessage({ id: 'global.zoom.out' })}
      />
      <i
        data-command="autoZoom"
        className="command iconfont icon-fit"
        title={formatMessage({ id: 'global.auto.zoom' })}
      />
      <i
        data-command="resetZoom"
        className="command iconfont icon-actual-size-o"
        title={formatMessage({ id: 'global.reset.zoom' })}
      />
      <span className="separator" />
      <i
        data-command="toBack"
        className="command iconfont icon-to-back"
        title={formatMessage({ id: 'global.to.back' })}
      />
      <i
        data-command="toFront"
        className="command iconfont icon-to-front"
        title={formatMessage({ id: 'global.to.front' })}
      />
      <span className="separator" />
      <i
        data-command="multiSelect"
        className="command iconfont icon-select"
        title={formatMessage({ id: 'global.multi.select' })}
      />
    </div>
  );
};

export default connect(({ flowEditor }: ConnectState) => ({
  ...flowEditor,
}))(Toolbar);
