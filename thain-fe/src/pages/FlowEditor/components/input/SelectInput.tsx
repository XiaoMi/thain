/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * @date 19-3-22 上午9:46
 * @author liangyongrui@xiaomi.com
 */

import { Popover, Select } from 'antd';
import React, { useEffect, useState } from 'react';
import { InputProps } from './InputProps';
const Option = Select.Option;

export default function SelectInput(props: InputProps) {
  const { updateGraph, attr, value, onBlurFunction, selectList, updateAttributes } = props;

  const [inputValue, setInputValue] = useState(value || '');

  useEffect(() => {
    if (selectList && (!value || selectList.map(t => t.id).indexOf(value) === -1)) {
      update(selectList[0].id);
    } else {
      update(value);
    }
  }, []);

  function update(v: string) {
    if (onBlurFunction) {
      onBlurFunction(attr, v, updateAttributes);
    } else {
      updateGraph(attr, v, updateAttributes);
    }
    setInputValue(v);
  }

  return (
    <Select mode="single" value={inputValue} onChange={update}>
      {(selectList || []).map(t => (
        <Option key={t.id} value={t.id}>
          <Popover content={t.id} overlayStyle={{ zIndex: 1200 }}>
            {t.id}
          </Popover>
        </Option>
      ))}
    </Select>
  );
}
