/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { useState } from 'react';
import { Button, Icon } from 'antd';
import { InputProps } from './InputProps';
// @ts-ignore
import FileBase64 from 'react-file-base64';
import '../../style/UploadBase64Input.less';

/**
 * @date 19-3-22 上午11:23
 * @author liangyongrui@xiaomi.com
 */

export default function UploadBase64Input(props: InputProps) {
  const { updateGraph, attr, onBlurFunction, updateAttributes } = props;

  const [name, setName] = useState('Click to Upload');

  function update(v: any) {
    if (onBlurFunction) {
      onBlurFunction(attr, v, updateAttributes);
    } else {
      updateGraph(attr, v, updateAttributes);
    }
  }

  function getFiles(files: any) {
    setName(files.name);
    update(files.base64);
  }

  return (
    <div className="a-upload">
      <FileBase64 onDone={getFiles} />
      <Icon type="upload" /> {name}
    </div>
  );
}
