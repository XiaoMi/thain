/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 *小驼峰转换下划线
 */
export function humpToLine(name: string) {
  return name.replace(/([A-Z])/g, '_$1').toUpperCase();
}
