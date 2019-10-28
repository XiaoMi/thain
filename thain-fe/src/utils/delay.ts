/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * 延迟函数
 * 执行callback 超过 ms 没有改变后
 */
export const delay = (() => {
  let timer: NodeJS.Timeout;
  return (callback: () => void, ms: number) => {
    clearTimeout(timer);
    timer = setTimeout(callback, ms);
  };
})();
