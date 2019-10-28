/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { useEffect, useState, ChangeEvent } from 'react';
import TextArea from 'antd/lib/input/TextArea';
import { InputProps } from './InputProps';

/**
 * @date 19-3-22 上午11:23
 * @author liangyongrui@xiaomi.com
 */

export default function TextareaInput(props: InputProps) {
  const { updateGraph, attr, value, onBlurFunction, updateAttributes } = props;

  const [inputValue, setInputValue] = useState(value || '');

  useEffect(() => {
    setInputValue(value);
  }, [value]);

  function update(v: any) {
    if (onBlurFunction) {
      onBlurFunction(attr, v, updateAttributes);
    } else {
      updateGraph(attr, v, updateAttributes);
    }
    setInputValue(v);
  }

  function changeInputValue(e: ChangeEvent<HTMLTextAreaElement>) {
    setInputValue(e.target.value);
  }

  return (
    <TextArea
      autosize={{ minRows: 1, maxRows: 6 }}
      value={inputValue}
      onChange={changeInputValue}
      onBlur={() => update(inputValue)}
    />
  );
}
