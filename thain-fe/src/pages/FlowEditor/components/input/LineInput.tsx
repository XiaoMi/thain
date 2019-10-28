/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { useEffect, useState } from 'react';
import { Input } from 'antd';
import { InputProps } from './InputProps';

/**
 * @date 19-3-22 上午11:23
 * @author liangyongrui@xiaomi.com
 */

export default function LineInput(props: InputProps) {
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

  return (
    <Input
      value={inputValue}
      onChange={e => setInputValue(e.target.value)}
      onBlur={() => update(inputValue)}
    />
  );
}
