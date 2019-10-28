/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 * @date 19-3-22 上午11:23
 * @author liangyongrui@xiaomi.com
 */

import React, { useEffect, useState } from 'react';
import 'braft-editor/dist/index.css';
import BraftEditor from 'braft-editor';
import 'braft-extensions/dist/table.css';
import { Modal, Input } from 'antd';
import { InputProps } from './InputProps';
import { formatMessage } from 'umi-plugin-react/locale';

export default function RichTextInput(props: InputProps) {
  const { updateGraph, attr, value, onBlurFunction, updateAttributes } = props;

  const [editorState, setEditorState] = useState(
    BraftEditor.createEditorState(value || '', { editorId: 'editor-1' }),
  );
  const [content, setContent] = useState(editorState.toText());
  const [rawContent, setRawContent] = useState(value);
  const [modalShow, setModalShow] = useState(false);

  useEffect(() => {
    setEditorState(BraftEditor.createEditorState(value));
    setContent(editorState.toText());
    setModalShow(false);
  }, [value]);

  function update() {
    if (onBlurFunction) {
      onBlurFunction(attr, editorState.toHTML(), updateAttributes);
    } else {
      updateGraph(attr, editorState.toHTML(), updateAttributes);
    }
    setRawContent(editorState.toHTML());
    setContent(editorState.toText());
    setModalShow(false);
  }

  function showModal() {
    setEditorState(BraftEditor.createEditorState(rawContent, { editorId: 'editor-1' }));
    setModalShow(true);
  }

  return (
    <div>
      <Input onClick={showModal} value={content} />
      <Modal
        title={formatMessage({ id: 'flow.edit' })}
        width="1000px"
        visible={modalShow}
        onCancel={() => setModalShow(false)}
        onOk={update}
      >
        <BraftEditor value={editorState} id="editor-1" onChange={setEditorState} />
      </Modal>
    </div>
  );
}
