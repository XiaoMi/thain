/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { ConnectState, ConnectProps } from '@/models/connect';
import { Guide, Chart, Geom, Coord, Axis, Legend, Tooltip } from 'bizcharts';
import DataSet from '@antv/data-set';
import React from 'react';
import { connect } from 'dva';
import {
  FlowSchedulingStatus,
  getScheduleStatusDesc,
  getScheduleStatusCode,
} from '@/enums/FlowSchedulingStatus';
import LoadingWrapper from './LoadingWrapper';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  scheduleStatusLoading: boolean;
  scheduleStatusCount: {
    status: FlowSchedulingStatus;
    count: number;
  }[];
  filterScheduleStatus: number[];
}
const ScheduleStatusCountChart: React.FC<Props> = ({
  scheduleStatusLoading,
  scheduleStatusCount,
  filterScheduleStatus,
  dispatch,
}) => {
  if (scheduleStatusLoading) {
    return LoadingWrapper(
      scheduleStatusLoading,
      formatMessage({ id: 'flow.schedule.status.chart.title' }),
      <div />,
    );
  }
  const { DataView } = DataSet;
  const { Html } = Guide;
  const dv = new DataView();
  const total = scheduleStatusCount.reduce(
    (pre, cur) => ({ status: FlowSchedulingStatus.NOT_SET, count: pre.count + cur.count }),
    { status: FlowSchedulingStatus.NOT_SET, count: 0 },
  ).count;
  dv.source(scheduleStatusCount)
    .transform({
      type: 'map',
      callback(row: { status: FlowSchedulingStatus; count: number }) {
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

  const changeFilter = (filterArr: number[], val: number, checked: boolean) => {
    if (!checked) {
      if (!filterArr.includes(val)) return [...filterArr, val];
      return filterArr;
    }
    return filterArr.filter(origin => origin !== val);
  };
  return LoadingWrapper(
    scheduleStatusLoading,
    formatMessage({ id: 'flow.schedule.status.chart.title' }),
    <Chart
      height={300}
      data={dv}
      scale={cols}
      padding={0}
      forceFit
      filter={[
        [
          'status',
          (val: string) => {
            return !filterScheduleStatus.includes(getScheduleStatusCode(val));
          },
        ],
      ]}
    >
      <Coord type={'theta'} radius={0.75} innerRadius={0.6} />
      <Axis name="percent" />
      <Legend
        offsetY={-40}
        textStyle={{
          fontSize: '12',
          fontWeight: 'bold',
        }}
        onClick={(ev: any) => {
          const value = ev.item.value;
          const checked = ev.checked;
          if (dispatch) {
            dispatch({
              type: 'dashboard/updateState',
              payload: {
                filterScheduleStatus: changeFilter(
                  filterScheduleStatus,
                  getScheduleStatusCode(value),
                  checked,
                ),
              },
            });
            dispatch({
              type: 'dashboard/fetchScheduleStatusCount',
            });
          }
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
  scheduleStatusLoading: dashboard.scheduleStatusLoading,
  scheduleStatusCount: dashboard.scheduleStatusCount,
  filterScheduleStatus: dashboard.filterScheduleStatus,
}))(ScheduleStatusCountChart);
