/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { Button, Col, DatePicker, Form, Icon, Input, Row, Select } from 'antd';
import React, { ChangeEvent, useState } from 'react';
import styles from './TableList.less';
import { connect } from 'dva';
import { ConnectProps, ConnectState } from '@/models/connect';
import { FlowListModelState } from './model';
import { FlowLastRunStatusGetEntries } from '@/enums/FlowLastRunStatus';
import { FlowSchedulingStatusGetEntries } from '@/enums/FlowSchedulingStatus';
import { RangePickerPresetRange } from 'antd/lib/date-picker/interface';
import { formatMessage } from 'umi-plugin-react/locale';

const { Option } = Select;
const { RangePicker } = DatePicker;
interface Props extends ConnectProps<{ flowId: number }> {
  flowList: FlowListModelState;
}
const SearchForm: React.FC<Props> = ({
  flowList: { flowId, lastRunStatus, flowName, scheduleStatus, updateTime, searchApp, createUser },
  dispatch,
}) => {
  const [formFlowId, setFormFlowId] = useState(flowId);
  const [formLastRunStatus, setFormLastRunStatus] = useState(lastRunStatus);
  const [formFlowName, setFormFlowName] = useState(flowName);
  const [formSearchApp, setFormSearchApp] = useState(searchApp);
  const [formCreateUser, setFormCreateUser] = useState(createUser);
  const [formScheduleStatus, setFormScheduleStatus] = useState(scheduleStatus);
  const [formUpdateTime, setFormUpdateTime] = useState(updateTime);
  const [showMore, setShowMore] = useState(false);

  function changeFormFlowId(event: ChangeEvent<HTMLInputElement>) {
    setFormFlowId(Number(event.target.value) || undefined);
  }

  function changeFormLastRunStatus(value: number) {
    setFormLastRunStatus(value);
  }

  function changeFormFlowName(event: ChangeEvent<HTMLInputElement>) {
    setFormFlowName(String(event.target.value) || undefined);
  }

  function changeFormSearchApp(event: ChangeEvent<HTMLInputElement>) {
    setFormSearchApp(String(event.target.value) || undefined);
  }

  function changeFormCreateUser(event: ChangeEvent<HTMLInputElement>) {
    setFormCreateUser(String(event.target.value) || undefined);
  }

  function changeFormScheduleStatus(value: number) {
    setFormScheduleStatus(value);
  }

  function changeFormUpdateTime(dates: RangePickerPresetRange) {
    setFormUpdateTime([dates[0] && dates[0].unix(), dates[1] && dates[1].unix()]);
  }

  function searchSubmit() {
    if (dispatch) {
      dispatch({
        type: 'flowList/fetchTable',
        payload: {
          flowId: formFlowId || 0,
          lastRunStatus: formLastRunStatus || 0,
          flowName: formFlowName || '',
          scheduleStatus: formScheduleStatus || 0,
          updateTime: formUpdateTime || [],
          searchApp: formSearchApp || '',
          createUser: formCreateUser || '',
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
              value={formFlowId}
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
              defaultValue={formLastRunStatus}
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
                value={formFlowName}
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
                value={formSearchApp}
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
                value={formCreateUser}
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
                defaultValue={formScheduleStatus}
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
                format="YYYY-MM-DD HH:mm"
                placeholder={['Start Time', 'End Time']}
                onOk={changeFormUpdateTime}
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

export default connect(({ flowList }: ConnectState) => ({
  flowList,
}))(SearchForm);
