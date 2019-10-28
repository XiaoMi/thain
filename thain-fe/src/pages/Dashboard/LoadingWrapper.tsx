/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import { Card, Spin } from 'antd';

const LoadingWrapper = (loading: boolean, title: string, innerChart: JSX.Element) => (
  <Card
    bordered={false}
    title={title}
    style={{
      marginTop: 20,
      textAlign: 'center',
    }}
    bodyStyle={{ padding: 1 }}
  >
    {loading ? (
      <Spin
        style={{
          height: 300,
          paddingTop: 120,
        }}
        tip="Loading..."
      />
    ) : (
      innerChart
    )}
  </Card>
);
export default LoadingWrapper;
