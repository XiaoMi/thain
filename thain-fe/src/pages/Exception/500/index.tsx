/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import { formatMessage } from 'umi-plugin-react/locale';
import Link from 'umi/link';
import Exception from '@/components/Exception';

export default () => (
  <Exception
    type="500"
    desc={formatMessage({ id: 'exception-500.description.500' })}
    linkElement={Link}
    backText={formatMessage({ id: 'exception-500.exception.back' })}
  />
);
