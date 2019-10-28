/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
export class EditorNode {
  public id = '';
  public jobId?: number;
  public index = 0;
  public attributes: { [props: string]: string } = {};
  public condition = '';
  public callbackUrl = '';
  public category = '';
  public color = '#1890ff';
  public label = '';
  public readonly shape = 'flow-rect';
  public readonly size = '80*48';
  public readonly type = 'node';
  public x = 0;
  public y = 0;
  public logs = '';
}
