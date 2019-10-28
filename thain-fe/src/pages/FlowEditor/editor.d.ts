/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
class Editor {
  public static Command: any;

  public static Detailpanel = class {
    constructor(props: any);
  };
  public static Itempanel = class {
    constructor(props: any);
  };
  public static Minimap = class {
    constructor(props: any);
  };
  public static Flow = class {
    constructor(props: any);
  };
  public static Toolbar = class {
    constructor(props: any);
  };
  public static Contextmenu = class {
    constructor(props: any);
  };

  public getCurrentPage(): any;
  public getComponentsByType(str: string);
  public executeCommand(props: any);
  public add(props: any);
}

export default Editor;
