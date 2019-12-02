/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { useEffect, useState } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Row, Col, Input } from 'antd';
import FormItem from 'antd/lib/form/FormItem';
import { ConnectState, ConnectProps } from '@/models/connect';
import { connect, useSelector, useDispatch } from 'dva';
import FlowExecutionTable from './FlowExecutionTable';
import { router } from 'umi';
import { LineChart } from './LineChart';
import { delay } from '@/utils/delay';
import { formatMessage } from 'umi-plugin-react/locale';

const FlowExecutionList: React.FC<ConnectProps<{ flowId: number }>> = ({ computedMatch }) => {
  const dispatch = useDispatch();
  const flowExecutionList = useSelector((state: ConnectState) => state.flowExecutionList);
  const flowId = computedMatch ? computedMatch.params.flowId : 0;
  const [inputFlowId, setInputFlowId] = useState(flowId);
  const { data } = flowExecutionList;

  const changeFlowId = (id: number) => {
    if (typeof id === 'number' && id > 0) {
      setInputFlowId(id);
      delay(() => {
        router.push(`/flow-execution/list/${id}`);
      }, 500);
    }
  };
  useEffect(() => {
    if (!flowId || flowId <= 0) {
      return;
    }
    dispatch({
      type: 'flowExecutionList/fetchTable',
      payload: {
        ...flowExecutionList,
        flowId,
      },
    });
  }, [flowId]);

  return (
    <PageHeaderWrapper title="Flow Execution">
      <Card bordered={false}>
        <Form layout="inline">
          <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
            <Col md={8} sm={24}>
              <FormItem label="Flow ID">
                <Input
                  allowClear
                  placeholder={formatMessage({ id: 'global.input.placeholder' })}
                  type="number"
                  value={inputFlowId}
                  onChange={e => changeFlowId(e.target.valueAsNumber)}
                />
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Card>
      <div style={{ marginTop: '20px' }} />
      <Card bordered={false}>
        <LineChart
          data={data.map(t => {
            return { time: t.updateTime - t.createTime };
          })}
        />
      </Card>
      <div style={{ marginTop: '20px' }} />
      <Card bordered={false}>
        <FlowExecutionTable />
      </Card>
    </PageHeaderWrapper>
  );
};

export default connect()(FlowExecutionList);
