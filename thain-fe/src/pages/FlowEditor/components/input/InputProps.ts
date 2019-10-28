/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

export interface InputProps {
  updateGraph: (key: string, value: string, updateAttributes?: boolean) => void;
  attr: string;
  value: any;
  updateAttributes?: boolean;
  selectList?: SelectOptions[];
  onBlurFunction?: Function;
}

export interface SelectOptions {
  name?: string;
  id: string;
}
