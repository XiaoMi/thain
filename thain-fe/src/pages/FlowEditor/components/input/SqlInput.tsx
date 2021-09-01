/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * @date 19-3-22 上午11:23
 * @author liangyongrui@xiaomi.com
 */

import React, { useEffect, useState } from 'react';
import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/sql/sql.js';

import { Controlled as CodeMirror } from 'react-codemirror2';
import { Modal, Input } from 'antd';
import { formatMessage } from 'umi-plugin-react/locale';
import { InputProps } from './InputProps';

export default function SqlInput(props: InputProps) {
  const { updateGraph, attr, value, onBlurFunction, updateAttributes } = props;

  const [content, setContent] = useState(value || '');
  const [modalValue, setModalValue] = useState(value || '');
  const [modalShow, setModalShow] = useState(false);

  useEffect(() => {
    setContent(value);
    setModalValue(value);
    setModalShow(false);
  }, [value]);

  function update() {
    if (onBlurFunction) {
      onBlurFunction(attr, modalValue, updateAttributes);
    } else {
      updateGraph(attr, modalValue, updateAttributes);
    }
    setContent(modalValue);
    setModalShow(false);
  }

  function showModal() {
    setModalValue(content);
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
        {modalShow && (
          <CodeMirror
            value={modalValue}
            options={{ lineNumbers: true }}
            onBeforeChange={(editor, data, v) => {
              setModalValue(v);
            }}
          />
        )}
      </Modal>
    </div>
  );
}
