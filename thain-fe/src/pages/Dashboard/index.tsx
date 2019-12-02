/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import * as React from 'react';
import { useEffect } from 'react';
import { Col, DatePicker, Row, Select } from 'antd';
import ScheduleStatusCountChart from './ScheduleStatusCountChart';
import JobExecutionStatusHistoryCountChart from './JobExecutionStatusHistoryCountChart';
import RunningFlowCountChart from './RunningFlowCountChart';
import IncreaseJobCountChart from './IncreaseJobCountChart';
import IncreaseFlowCountChart from './IncreaseFlowCountChart';
import RunningJobCountChart from './RunningJobCountChart';
import JobExecutionStatusCountChart from './JobExecutionStatusCountChart';
import FlowSourceCountChart from './FlowSourceCountChart';
import FlowExecutionStatusCountChart from './FlowExecutionStatusCountChart';
import { connect } from 'dva';
import { ConnectState, ConnectProps } from '@/models/connect';
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { RangePickerPresetRange } from 'antd/lib/date-picker/interface';
import moment from 'moment';
import { formatMessage } from 'umi-plugin-react/locale';
const { RangePicker } = DatePicker;

const colLayout = {
  xs: 24,
  sm: 24,
  md: 12,
  lg: 12,
  xl: 12,
  xxl: 6,
};
interface Props extends ConnectProps {
  firstHistoryPeriod: number[];
  secondHistoryPeriod: number[];
  filterScheduleStatus: FlowSchedulingStatus[];
  filterSource: string[];
  maxPointNum: number;
}
const Analysis: React.FC<Props> = ({
  dispatch,
  firstHistoryPeriod,
  secondHistoryPeriod,
  filterScheduleStatus,
  filterSource,
  maxPointNum,
}) => {
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/fetchScheduleStatusCount',
      });
    }
  }, [filterSource]);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/fetchFlowSourceCount',
      });
    }
  }, [filterScheduleStatus]);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/fetchRunningFlowCount',
      });
      dispatch({
        type: 'dashboard/fetchRunningJobCount',
      });
    }
  }, [filterScheduleStatus, filterSource]);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/fetchFlowExecutionStatusCount',
      });
      dispatch({
        type: 'dashboard/fetchJobExecutionStatusCount',
      });
      dispatch({
        type: 'dashboard/fetchIncreaseFlowCount',
      });
      dispatch({
        type: 'dashboard/fetchIncreaseJobCount',
      });
    }
  }, [firstHistoryPeriod]);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/fetchStatusHistoryCount',
      });
    }
  }, [secondHistoryPeriod, maxPointNum]);

  const firstRangeChange = (dates: RangePickerPresetRange) => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/updateState',
        payload: { firstHistoryPeriod: [dates[0] && dates[0].unix(), dates[1] && dates[1].unix()] },
      });
    }
  };

  const secondRangeChange = (dates: RangePickerPresetRange) => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/updateState',
        payload: {
          secondHistoryPeriod: [dates[0] && dates[0].unix(), dates[1] && dates[1].unix()],
        },
      });
    }
  };
  const maxPointNumChange = (value: number) => {
    if (dispatch) {
      dispatch({
        type: 'dashboard/updateState',
        payload: {
          maxPointNum: value,
        },
      });
    }
  };
  const { Option } = Select;
  return (
    <div style={{ marginLeft: 20, marginRight: 20 }}>
      <div>
        <Row gutter={20}>
          <Col {...colLayout}>
            <ScheduleStatusCountChart />
          </Col>
          <Col {...colLayout}>
            <FlowSourceCountChart />
          </Col>
          <Col {...colLayout}>
            <RunningFlowCountChart />
          </Col>
          <Col {...colLayout}>
            <RunningJobCountChart />
          </Col>
        </Row>
        <Row gutter={20}>
          <Col span={8}>
            <span>{formatMessage({ id: 'common.statistical.period' })}：</span>
            <RangePicker
              defaultValue={[
                moment.unix(firstHistoryPeriod[0]),
                moment.unix(firstHistoryPeriod[1]),
              ]}
              ranges={{
                Today: [moment().startOf('day'), moment().endOf('day')],
                Yesterday: [
                  moment()
                    .subtract(1, 'd')
                    .startOf('day'),
                  moment()
                    .subtract(1, 'd')
                    .endOf('day'),
                ],
                'This Week': [moment().startOf('week'), moment().endOf('week')],
              }}
              style={{ marginTop: 20 }}
              showTime={{ format: 'HH:mm' }}
              format="YYYY-MM-DD HH:mm"
              placeholder={['Start Time', 'End Time']}
              onOk={firstRangeChange}
            />
          </Col>
        </Row>
        <Row gutter={20}>
          <Col {...colLayout}>
            <FlowExecutionStatusCountChart />
          </Col>
          <Col {...colLayout}>
            <JobExecutionStatusCountChart />
          </Col>
          <Col {...colLayout}>
            <IncreaseFlowCountChart />
          </Col>
          <Col {...colLayout}>
            <IncreaseJobCountChart />
          </Col>
        </Row>
        <Row>
          <Col xs={24} sm={12} md={12} lg={10} xl={10}>
            <span>{formatMessage({ id: 'common.statistical.period' })}：</span>
            <RangePicker
              defaultValue={[
                moment.unix(secondHistoryPeriod[0]),
                moment.unix(secondHistoryPeriod[1]),
              ]}
              ranges={{
                Today: [moment().startOf('day'), moment().endOf('day')],
                Yesterday: [
                  moment()
                    .subtract(1, 'd')
                    .startOf('day'),
                  moment()
                    .subtract(1, 'd')
                    .endOf('day'),
                ],
                'This Week': [moment().startOf('week'), moment().endOf('week')],
              }}
              style={{ marginTop: 20 }}
              showTime={{ format: 'HH:mm' }}
              format="YYYY-MM-DD HH:mm"
              placeholder={['Start Time', 'End Time']}
              onOk={secondRangeChange}
            />
          </Col>
          <Col xs={24} sm={12} md={12} lg={10} xl={10}>
            <span>{formatMessage({ id: 'common.polygon.max.num' })}：</span>
            <Select
              showSearch
              style={{ marginTop: 20, width: 200 }}
              defaultValue={maxPointNum}
              optionFilterProp="children"
              onChange={maxPointNumChange}
            >
              {[...Array(30).keys()].map(num => {
                return <Option key={num + 1}>{num + 1}</Option>;
              })}
            </Select>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <JobExecutionStatusHistoryCountChart />
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default connect(({ dashboard }: ConnectState) => ({
  firstHistoryPeriod: dashboard.firstHistoryPeriod,
  secondHistoryPeriod: dashboard.secondHistoryPeriod,
  filterScheduleStatus: dashboard.filterScheduleStatus,
  filterSource: dashboard.filterSource,
  maxPointNum: dashboard.maxPointNum,
}))(Analysis);
