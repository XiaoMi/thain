/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
export class EditorEdge {
  public id: string = '';
  public index: number = 0;
  public readonly shape = 'flow-smoot';
  public source: string = '';
  public target: string = '';
  public sourceAnchor: number = 0;
  public targetAnchor: number = 0;
}
