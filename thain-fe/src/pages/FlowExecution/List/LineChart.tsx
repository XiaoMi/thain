/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { Chart, Geom, Axis, Tooltip, Legend } from 'bizcharts';
import React from 'react';

const cols = {
  id: {
    range: [0, 1],
  },
};

export function LineChart(props: { data: { x?: number; time: number }[] }) {
  const { data } = props;
  let i = 0;
  data.forEach(t => (t.x = i++));
  return (
    <div style={{ marginBottom: '-60px' }}>
      <Chart height={300} data={data} scale={cols} forceFit>
        <Legend />
        <Axis name="x" />
        <Axis name="time" />
        <Tooltip
          crosshairs={{
            type: 'y',
          }}
        />
        <Geom type="line" position="x*time" size={2} shape="smooth" />
        <Geom
          type="point"
          position="x*time"
          size={3}
          shape="circle"
          color="city"
          style={{
            stroke: '#fff',
          }}
        />
      </Chart>
    </div>
  );
}
