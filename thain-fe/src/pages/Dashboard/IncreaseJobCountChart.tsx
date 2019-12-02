/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { ConnectState, ConnectProps } from '@/models/connect';
import React from 'react';
import { connect } from 'dva';
import { Statistic } from 'antd';
import LoadingWrapper from './LoadingWrapper';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  increaseJobCountLoading: boolean;
  increaseJobCount: number;
}
const IncreaseJobCountChart: React.FC<Props> = ({ increaseJobCount, increaseJobCountLoading }) => {
  return LoadingWrapper(
    increaseJobCountLoading,
    formatMessage({ id: 'job.increase.chart.title' }),
    <Statistic
      style={{ height: 300, paddingTop: 100 }}
      valueStyle={{ fontSize: 70 }}
      value={increaseJobCount}
    />,
  );
};

export default connect(({ dashboard }: ConnectState) => ({
  increaseJobCountLoading: dashboard.increaseJobCountLoading,
  increaseJobCount: dashboard.increaseJobCount,
}))(IncreaseJobCountChart);
