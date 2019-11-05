/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { Button, Col, DatePicker, Form, Icon, Input, Row, Select } from 'antd';
import React, { ChangeEvent, useState } from 'react';
import styles from './TableList.less';
import { connect } from 'dva';
import { ConnectProps } from '@/models/connect';
import { FlowSearch } from './model';
import { FlowLastRunStatusGetEntries } from '@/enums/FlowLastRunStatus';
import { FlowSchedulingStatusGetEntries } from '@/enums/FlowSchedulingStatus';
import { RangePickerPresetRange } from 'antd/lib/date-picker/interface';
import { formatMessage } from 'umi-plugin-react/locale';
import { router } from 'umi';
import moment from 'moment';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface Props extends ConnectProps<{ flowId: number }> {
  condition: FlowSearch;
  setCondition: Function;
}
export function splieceParam(object: FlowSearch) {
  let paramUrl = '';
  for (const [key, value] of Object.entries(object)) {
    let url = '';
    if (typeof value === 'string' && value !== '') {
      url = `${key}=${value}&`;
    } else if (typeof value === 'number' && value !== 0) {
      url = `${key}=${value}&`;
    } else if (typeof value === 'object' && value.length === 2) {
      url = `updateTime=[${value[0]},${value[1]}]&`;
    } else if (typeof value === 'boolean') {
      const status = value === true ? 1 : 0;
      url = `sortOrderDesc=${status}&`;
    }
    paramUrl = `${paramUrl}${url}`;
  }
  if (paramUrl !== '') {
    paramUrl = paramUrl.substring(0, paramUrl.length - 1);
  }
  return paramUrl;
}
const SearchForm: React.FC<Props> = ({ condition, dispatch, setCondition }) => {
  const [showMore, setShowMore] = useState(false);
  const [dateDefault] = useState(initDate());
  const format = 'YYYY-MM-DD HH:mm';
  function initDate() {
    let dateList: undefined | undefined[] | [moment.Moment, moment.Moment]; // = [undefined, undefined];
    if (condition.updateTime.length === 2) {
      dateList = [
        moment(new Date(condition.updateTime[0]).toLocaleDateString(), format),
        moment(new Date(condition.updateTime[1]).toLocaleDateString(), format),
      ];
    }
    return dateList;
  }

  function changeFormFlowId(event: ChangeEvent<HTMLInputElement>) {
    setCondition({ ...condition, flowId: Number(event.target.value) });
  }

  function changeFormLastRunStatus(value: number) {
    setCondition({ ...condition, lastRunStatus: value });
  }

  function changeFormFlowName(event: ChangeEvent<HTMLInputElement>) {
    setCondition({ ...condition, flowName: event.target.value });
  }

  function changeFormSearchApp(event: ChangeEvent<HTMLInputElement>) {
    setCondition({ ...condition, searchApp: event.target.value });
  }

  function changeFormCreateUser(event: ChangeEvent<HTMLInputElement>) {
    setCondition({ ...condition, createUser: event.target.value });
  }

  function changeFormScheduleStatus(value: number) {
    setCondition({ ...condition, scheduleStatus: value });
  }

  function changeFormUpdateTime(dates: RangePickerPresetRange) {
    setCondition({
      ...condition,
      updateTime: [dates[0] && dates[0].unix(), dates[1] && dates[1].unix()],
    });
  }

  function searchSubmit() {
    if (dispatch) {
      router.push(`/flow/list/?${splieceParam(condition)}`);
      setCondition({ ...condition });
      dispatch({
        type: 'flowList/fetchTable',
        payload: {
          ...condition,
        },
      });
    }
  }
  return (
    <Form layout="inline">
      <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
        <Col md={8} sm={24}>
          <Form.Item label="Flow ID">
            <Input
              allowClear
              placeholder={formatMessage({ id: 'global.input.placeholder' })}
              type="number"
              value={condition.flowId}
              onChange={changeFormFlowId}
            />
          </Form.Item>
        </Col>
        <Col md={8} sm={24}>
          <Form.Item label={formatMessage({ id: 'flow.last.running.status' })}>
            <Select
              placeholder={formatMessage({ id: 'global.select.placeholder' })}
              allowClear
              style={{ width: 150 }}
              defaultValue={condition.lastRunStatus}
              onChange={changeFormLastRunStatus}
            >
              {FlowLastRunStatusGetEntries().map(([key, value]) => {
                return (
                  <Option key={key} value={value}>
                    {key}
                  </Option>
                );
              })}
            </Select>
          </Form.Item>
        </Col>
        <Col md={8} sm={24}>
          <Form.Item>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit" onClick={searchSubmit}>
                {formatMessage({ id: 'flow.retrieve' })}
              </Button>
              <a
                style={{ marginLeft: 10 }}
                onClick={() => {
                  setShowMore(!showMore);
                }}
              >
                {showMore
                  ? formatMessage({ id: 'global.tagSelect.collapse' })
                  : formatMessage({ id: 'global.tagSelect.expand' })}
                <Icon type="down" />
              </a>
            </span>
          </Form.Item>
        </Col>
      </Row>
      {showMore ? (
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <Form.Item label={formatMessage({ id: 'flow.name' })}>
              <Input
                allowClear
                placeholder={formatMessage({ id: 'global.input.placeholder' })}
                type="string"
                value={condition.flowName}
                onChange={changeFormFlowName}
              />
            </Form.Item>
          </Col>
          <Col md={8} sm={24}>
            <Form.Item label={formatMessage({ id: 'flow.app' })}>
              <Input
                allowClear
                placeholder={formatMessage({ id: 'global.input.placeholder' })}
                type="string"
                value={condition.searchApp}
                onChange={changeFormSearchApp}
              />
            </Form.Item>
          </Col>
          <Col md={8} sm={24}>
            <Form.Item label={formatMessage({ id: 'flow.create.user' })}>
              <Input
                allowClear
                placeholder={formatMessage({ id: 'global.input.placeholder' })}
                type="string"
                value={condition.createUser}
                onChange={changeFormCreateUser}
              />
            </Form.Item>
          </Col>
          <Col md={8} sm={24}>
            <Form.Item label={formatMessage({ id: 'flow.schedule.status' })}>
              <Select
                placeholder={formatMessage({ id: 'global.select.placeholder' })}
                allowClear
                style={{ width: 150 }}
                defaultValue={condition.scheduleStatus}
                onChange={changeFormScheduleStatus}
              >
                {FlowSchedulingStatusGetEntries().map(([key, value]) => {
                  return (
                    <Option key={key} value={value}>
                      {key}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
          </Col>
          <Col md={8} sm={24}>
            <Form.Item label={formatMessage({ id: 'flow.status.update.time' })}>
              <RangePicker
                showTime={{ format: 'HH:mm' }}
                format={format}
                placeholder={['Start Time', 'End Time']}
                onOk={changeFormUpdateTime}
                defaultValue={dateDefault}
                onChange={date => {
                  if (date.length === 0) {
                    setCondition({ ...condition, updateTime: [] });
                  }
                }}
              />
            </Form.Item>
          </Col>
        </Row>
      ) : (
        ''
      )}
    </Form>
  );
};

export default connect()(SearchForm);
