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
  increaseFlowCountLoading: boolean;
  increaseFlowCount: number;
}
const IncreaseFlowCountChart: React.FC<Props> = ({
  increaseFlowCount,
  increaseFlowCountLoading,
}) => {
  return LoadingWrapper(
    increaseFlowCountLoading,
    formatMessage({ id: 'flow.increase.chart.title' }),
    <Statistic
      style={{ height: 300, paddingTop: 100 }}
      valueStyle={{ fontSize: 70 }}
      value={increaseFlowCount}
    />,
  );
};

export default connect(({ dashboard }: ConnectState) => ({
  increaseFlowCountLoading: dashboard.increaseFlowCountLoading,
  increaseFlowCount: dashboard.increaseFlowCount,
}))(IncreaseFlowCountChart);
