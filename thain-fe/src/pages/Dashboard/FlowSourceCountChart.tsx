/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { ConnectState, ConnectProps } from '@/models/connect';
import { Axis, Chart, Coord, Geom, Guide, Legend, Tooltip } from 'bizcharts';
import DataSet from '@antv/data-set';
import React from 'react';
import { connect } from 'dva';
import LoadingWrapper from './LoadingWrapper';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  flowSourceCountLoading: boolean;
  flowSourceCount: {
    source: string;
    count: number;
  }[];
  filterSource: string[];
}
const FlowSourceCountChart: React.FC<Props> = ({
  flowSourceCount,
  filterSource,
  flowSourceCountLoading,
  dispatch,
}) => {
  if (flowSourceCountLoading) {
    return LoadingWrapper(
      flowSourceCountLoading,
      formatMessage({ id: 'flow.source.chart.title' }),
      <div />,
    );
  }
  const { DataView } = DataSet;
  const { Html } = Guide;
  const total = flowSourceCount.reduce(
    (pre, cur) => ({ source: '', count: pre.count + cur.count }),
    { source: '', count: 0 },
  ).count;
  const dv = new DataView();

  dv.source(flowSourceCount).transform({
    type: 'percent',
    field: 'count',
    dimension: 'source',
    as: 'percent',
  });
  const cols = {
    percent: {
      formatter: (val: number) => {
        return `${(val * 100).toFixed(2)}%`;
      },
    },
  };
  const changeFilter = (filterArr: string[], val: string, checked: boolean) => {
    if (!checked) {
      if (!filterArr.includes(val)) return [...filterArr, val];
      return filterArr;
    }
    return filterArr.filter(origin => origin !== val);
  };
  return LoadingWrapper(
    flowSourceCountLoading,
    formatMessage({ id: 'flow.source.chart.title' }),
    <Chart
      height={300}
      data={dv}
      scale={cols}
      padding={0}
      forceFit
      filter={[
        [
          'source',
          (val: string) => {
            return !filterSource.includes(val);
          },
        ],
      ]}
    >
      <Coord type="theta" radius={0.75} innerRadius={0.6} />
      <Axis name="percent" />
      <Legend
        itemFormatter={(item: string) => {
          if (item.length > 10) {
            return `${item.substr(0, 8)}...`;
          }
          return item;
        }}
        position="right-center"
        offsetY={0}
        offsetX={-100}
        textStyle={{
          fontSize: '12',
          fontWeight: 'bold',
        }}
        onClick={(ev: any) => {
          const {value} = ev.item;
          const {checked} = ev;
          if (dispatch) {
            dispatch({
              type: 'dashboard/updateState',
              payload: {
                filterSource: changeFilter(filterSource, value, checked),
              },
            });
            dispatch({
              type: 'dashboard/fetchFlowSourceCount',
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
        color="source"
        tooltip={[
          'source*percent',
          (source, percent) => {
            percent = `${(percent * 100).toFixed(2)  }%`;
            return {
              name: source,
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
  flowSourceCountLoading: dashboard.flowSourceCountLoading,
  flowSourceCount: dashboard.flowSourceCount,
  filterSource: dashboard.filterSource,
}))(FlowSourceCountChart);
