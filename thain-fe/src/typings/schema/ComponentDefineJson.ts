/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * https://app.quicktype.io/#l=schema 这个网站生成json
 *
 * 组件前端 schema
 *
 */
export type ComponentDefineJson = Array<{
  /**
   * 传到后端的key
   */
  property: string;
  /**
   * 是否必填，false 或不写 则为不是必填
   */
  required?: boolean;
  /**
   * 输入框前面的标识，不写默认用property
   */
  label?: string;
  input:
    | {
        id: 'line' | 'textarea' | 'sql' | 'shell' | 'richText';
      }
    | {
        id: 'select';
        options: Array<{
          /**
           * 属性的值
           */
          id: string;
          /**
           * 下拉框中的候选项，不写用id代替
           */
          name?: string;
        }>;
      };
}>;
