/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
interface ApiResult<T = any> {
  status: number;
  message: string;
  data: T;
}

class TableResult<T = any> {
  public data: T[] = [];
  public count: number = 0;
  public page: number = 0;
  public pageSize: number = 0;
}

export { ApiResult, TableResult };
