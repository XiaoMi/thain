/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
/**
 * @date 2019年02月28日10:43:33
 * @author liangyongrui@xiaomi.com
 *
 * 通用hooks
 */

import { useEffect } from 'react';

/**
 * 相当于之前的componentDidMount
 */
export const useMount = (fn: () => void) => useEffect(() => fn(), []);
/**
 * 相当于之前的componentWillUnmount
 */
export const useUnmount = (fn: () => void) => useEffect(() => fn, []);
