/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import ConnectState, { ConnectProps } from '@/models/connect';
import React from 'react';
import { connect } from 'dva';
import { Statistic } from 'antd';
import LoadingWrapper from './LoadingWrapper';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  runningJobCountLoading: boolean;
  runningJobCount: number;
}
const RunningJobCountChart: React.FC<Props> = ({ runningJobCount, runningJobCountLoading }) => {
  return LoadingWrapper(
    runningJobCountLoading,
    formatMessage({ id: 'job.running.chart.title' }),
    <Statistic
      style={{ height: 300, paddingTop: 100 }}
      valueStyle={{ fontSize: 70 }}
      value={runningJobCount}
    />,
  );
};

export default connect(({ dashboard }: ConnectState) => ({
  runningJobCountLoading: dashboard.runningJobCountLoading,
  runningJobCount: dashboard.runningJobCount,
}))(RunningJobCountChart);
