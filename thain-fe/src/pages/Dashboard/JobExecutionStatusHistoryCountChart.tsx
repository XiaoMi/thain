/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import { Chart, Geom, Axis, Tooltip, Legend } from 'bizcharts';
import { ConnectState, ConnectProps } from '@/models/connect';
import { connect } from 'dva';
import LoadingWrapper from './LoadingWrapper';
import moment from 'moment';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  statusHistoryCountLoading: boolean;
  statusHistoryCount: {
    status: number;
    time: string;
    count: number;
  }[];
}

const JobExecutionStatusHistoryCountChart: React.FC<Props> = ({
  statusHistoryCount,
  statusHistoryCountLoading,
}) => {
  if (statusHistoryCountLoading) {
    return LoadingWrapper(
      statusHistoryCountLoading,
      formatMessage({ id: 'job.execution.history.chart.title' }),
      <div />,
    );
  }
  const cols = {
    month: {
      range: [0, 1],
    },
  };

  const formatData = statusHistoryCount.map(countData => {
    const longTime = countData.time;
    const time = longTime
      .split('~')
      .map(t => moment.unix(Number(t)).format('YYYY-MM-DD HH:mm:ss'))
      .reduce((pre, cur) => {
        return pre + '\n ~ \n' + cur;
      });
    return {
      ...countData,
      time,
      status:
        countData.status === 2
          ? formatMessage({ id: 'common.success' })
          : formatMessage({ id: 'common.failed' }),
    };
  });
  return LoadingWrapper(
    statusHistoryCountLoading,
    formatMessage({ id: 'job.execution.history.chart.title' }),
    <Chart height={400} data={formatData} scale={cols} forceFit>
      <Legend />
      <Axis name="time" label={{ offset: 30 }} />
      <Axis name="count" />
      <Tooltip
        crosshairs={{
          type: 'y',
        }}
      />
      <Geom type="line" position="time*count" size={2} color={'status'} shape={'smooth'} />
      <Geom
        type="point"
        position="time*count"
        size={4}
        shape={'circle'}
        color={'stataus'}
        style={{
          stroke: '#fff',
        }}
      />
    </Chart>,
  );
};

export default connect(({ dashboard }: ConnectState) => ({
  statusHistoryCountLoading: dashboard.statusHistoryCountLoading,
  statusHistoryCount: dashboard.statusHistoryCount,
}))(JobExecutionStatusHistoryCountChart);
