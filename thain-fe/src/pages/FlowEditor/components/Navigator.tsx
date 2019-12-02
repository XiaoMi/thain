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
import '../style/navigator.less';
import { ConnectProps, ConnectState } from '@/models/connect';
import { connect } from 'dva';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
}

const Navigator: React.FC<Props> = ({ editor }) => {
  const miniMapContainer = useRef(null);
  useEffect(() => {
    if (editor) {
      const miniMap = new Editor.Minimap({ container: miniMapContainer.current });
      editor.add(miniMap);
    }
  }, [editor]);

  return (
    <div id="navigator">
      <div className="panel-title">{formatMessage({ id: 'global.navigation' })}</div>
      <div id="minimap" ref={miniMapContainer} />
    </div>
  );
};

export default connect(({ flowEditor }: ConnectState) => ({
  ...flowEditor,
}))(Navigator);
