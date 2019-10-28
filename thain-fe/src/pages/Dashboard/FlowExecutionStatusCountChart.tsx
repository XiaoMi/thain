/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import ConnectState, { ConnectProps } from '@/models/connect';
import { Guide, Chart, Geom, Coord, Axis, Legend, Tooltip } from 'bizcharts';
import DataSet from '@antv/data-set';
import React from 'react';
import { connect } from 'dva';
import { FlowExecutionStatus, getScheduleStatusDesc } from '@/enums/FlowExecutionStatus';
import LoadingWrapper from './LoadingWrapper';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  flowExecutionStatusCountLoading: boolean;
  flowExecutionStatusCount: {
    status: FlowExecutionStatus;
    count: number;
  }[];
}
const FlowExecutionStatusCountChart: React.FC<Props> = ({
  flowExecutionStatusCount,
  flowExecutionStatusCountLoading,
}) => {
  if (flowExecutionStatusCountLoading) {
    return LoadingWrapper(
      flowExecutionStatusCountLoading,
      formatMessage({ id: 'flow.execution.status.chart.title' }),
      <div />,
    );
  }
  const { DataView } = DataSet;
  const { Html } = Guide;
  const total = flowExecutionStatusCount.reduce(
    (pre, cur) => ({ status: FlowExecutionStatus.SUCCESS, count: pre.count + cur.count }),
    { status: FlowExecutionStatus.SUCCESS, count: 0 },
  ).count;
  const dv = new DataView();
  dv.source(flowExecutionStatusCount)
    .transform({
      type: 'map',
      callback(row: { status: FlowExecutionStatus; count: number }) {
        return { status: getScheduleStatusDesc(row.status), count: row.count };
      },
    })
    .transform({
      type: 'percent',
      field: 'count',
      dimension: 'status',
      as: 'percent',
    });
  const cols = {
    percent: {
      formatter: (val: number) => {
        return (val * 100).toFixed(2) + '%';
      },
    },
  };

  return LoadingWrapper(
    flowExecutionStatusCountLoading,
    formatMessage({ id: 'flow.execution.status.chart.title' }),
    <Chart height={300} data={dv} scale={cols} padding={0} forceFit>
      <Coord type={'theta'} radius={0.75} innerRadius={0.6} />
      <Axis name="percent" />
      <Legend
        offsetY={-40}
        textStyle={{
          fontSize: '12',
          fontWeight: 'bold',
        }}
      />
      <Tooltip
        showTitle={false}
        itemTpl='<li><span style="background-color:{color};" class="g2-tooltip-marker"></span><b style="font-size:14px;">{name}: {value}</b></li>'
      />
      <Guide>
        <Html
          position={['50%', '50%']}
          html={`<div style="color:#8c8c8c;font-size:1.16em;text-align: center;width: 10em;">${formatMessage(
            { id: 'common.total' },
          )}<br><span style="color:#262626;font-size:2.5em">${total}</span></div>`}
          alignX="middle"
          alignY="middle"
        />
      </Guide>
      <Geom
        type="intervalStack"
        position="percent"
        color="status"
        tooltip={[
          'status*percent',
          (status, percent) => {
            percent = (percent * 100).toFixed(2) + '%';
            return {
              name: status,
              value: percent,
            };
          },
        ]}
        style={{
          stroke: '#fff',
        }}
      />
    </Chart>,
  );
};

export default connect(({ dashboard }: ConnectState) => ({
  flowExecutionStatusCountLoading: dashboard.flowExecutionStatusCountLoading,
  flowExecutionStatusCount: dashboard.flowExecutionStatusCount,
}))(FlowExecutionStatusCountChart);
