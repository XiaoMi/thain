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
import moment from 'moment';
import FlowTable from './FlowTable';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface Props extends ConnectProps<{ flowId: number }> {
  condition: FlowSearch;
  setCondition: Function;
}
const SearchForm: React.FC<Props> = ({ condition, setCondition }) => {
  const [showMore, setShowMore] = useState(false);
  const [model, setModel] = useState<FlowSearch>(condition);
  const [searchDate, setSearchDate] = useState(initDate());
  const format = 'yyyy-MM-DD HH:mm';
  function initDate() {
    let dateList: undefined | undefined[] | [moment.Moment, moment.Moment];
    if (model.updateTime && model.updateTime.length === 2) {
      dateList = [
        moment(new Date(condition.updateTime[0] * 1000).toLocaleDateString(), format),
        moment(new Date(condition.updateTime[1] * 1000).toLocaleDateString(), format),
      ];
    }
    return dateList;
  }

  function changeFormFlowId(event: ChangeEvent<HTMLInputElement>) {
    setModel({ ...model, flowId: Number(event.target.value) });
  }

  function changeFormLastRunStatus(value: number) {
    setModel({ ...model, lastRunStatus: value });
  }

  function changeFormFlowName(event: ChangeEvent<HTMLInputElement>) {
    setModel({ ...model, flowName: event.target.value });
  }

  function changeFormSearchApp(event: ChangeEvent<HTMLInputElement>) {
    setModel({ ...model, searchApp: event.target.value });
  }

  function changeFormCreateUser(event: ChangeEvent<HTMLInputElement>) {
    setModel({ ...model, createUser: event.target.value });
  }

  function changeFormScheduleStatus(value: number) {
    setModel({ ...model, scheduleStatus: value });
  }

  function changeFormUpdateTime(dates: RangePickerPresetRange) {
    setModel({
      ...model,
      updateTime: [dates[0] && dates[0].unix(), dates[1] && dates[1].unix()],
    });
  }

  function searchSubmit() {
    setCondition({ ...model });
  }
  return (
    <div>
      <Form layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <Form.Item label="Flow ID">
              <Input
                allowClear
                placeholder={formatMessage({ id: 'global.input.placeholder' })}
                type="number"
                value={model.flowId}
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
                onChange={changeFormLastRunStatus}
                value={model.lastRunStatus}
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
                  value={model.flowName}
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
                  value={model.searchApp}
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
                  value={model.createUser}
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
                  defaultValue={model.scheduleStatus}
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
                  value={searchDate}
                  onChange={date => {
                    if (date.length < 2 || date[0] === undefined || date[1] === undefined) {
                      setModel({ ...model, updateTime: [] });
                      setSearchDate(undefined);
                      return;
                    }
                    setSearchDate([date[0], date[1]]);
                  }}
                />
              </Form.Item>
            </Col>
          </Row>
        ) : (
          ''
        )}
      </Form>
      <div style={{ marginTop: '20px', overflow: '320px' }}>
        <FlowTable condition={condition} setCondition={setCondition} modelChange={setModel} />
      </div>
    </div>
  );
};

export default connect()(SearchForm);
