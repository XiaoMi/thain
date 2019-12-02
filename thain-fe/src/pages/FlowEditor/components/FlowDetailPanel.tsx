/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 *
 * date 2019年03月19日
 * @author liangyongrui@xiaomi.com
 */
import Editor from '../editor';
import React, { useState } from 'react';
import { Checkbox, Modal, TimePicker, Form, Input } from 'antd';
import { CheckboxChangeEvent } from 'antd/lib/checkbox';
import { ConnectProps, ConnectState } from '@/models/connect';
import { connect } from 'dva';
import { FlowAttributes } from '@/pages/FlowEditor/model';
import LineInput from './input/LineInput';
import TextareaInput from './input/TextareaInput';
import Button from 'antd/es/button/button';
import moment, { Moment } from 'moment';
import { formatMessage } from 'umi-plugin-react/locale';

function getDaySeconds(time: Moment | undefined) {
  if (!time) {
    return 0;
  }
  return time.second() + time.minute() * 60 + time.hour() * 60 * 60;
}

function daySecondsToMoment(second: number | undefined) {
  if (!second) {
    return moment({ second: 0, minute: 0, hour: 3 });
  }
  const s = second % 60;
  const m = Math.floor(second / 60) % 60;
  const h = Math.floor(second / 60 / 60) % 24;
  return moment({ second: s, minute: m, hour: h });
}

interface Props extends ConnectProps<{ flowId: number }> {
  readonly editor?: Editor;
  updateGraph: (key: string, value: string, updateAttributes?: boolean) => void;
  flowAttributes: FlowAttributes;
  flowId: number;
}
const DetailPanel: React.FC<Props> = ({
  editor,
  flowAttributes,
  flowId,
  updateGraph,
  dispatch,
}) => {
  if (flowId && !flowAttributes.name) {
    return <div />;
  }
  const onBlurFunction = (attr: string, value: any) => {
    if (dispatch) {
      dispatch({
        type: 'flowEditor/changeFlowAttributes',
        payload: {
          [attr]: value,
        },
      });
    }
  };

  function toggleGrid(e: CheckboxChangeEvent) {
    const page = editor!.getCurrentPage();
    if (e.target.checked) {
      page.showGrid();
    } else {
      page.hideGrid();
    }
  }

  const [slaModalShow, setSlaModalShow] = useState(false);
  const [slaTime, setSlaTime] = useState(daySecondsToMoment(flowAttributes.slaDuration));
  const [slaEmail, setSlaEmail] = useState(flowAttributes.slaEmail);

  return (
    <div className="detailpanel">
      <div className="block-container">
        <div>
          <Checkbox onChange={toggleGrid}>{formatMessage({ id: 'flow.grid.align' })}</Checkbox>
        </div>
        <div>
          {formatMessage({ id: 'flow.name' })} <span style={{ color: 'red' }}>*</span>
          <LineInput
            updateGraph={updateGraph}
            attr="name"
            value={flowAttributes.name}
            onBlurFunction={onBlurFunction}
          />
        </div>
        <div>
          {formatMessage({ id: 'flow.cron' })}
          <LineInput
            updateGraph={updateGraph}
            attr="cron"
            value={flowAttributes.cron}
            onBlurFunction={onBlurFunction}
          />
        </div>
        <div>
          {formatMessage({ id: 'flow.status.callback.url' })}
          <TextareaInput
            updateGraph={updateGraph}
            attr="callbackUrl"
            value={flowAttributes.callbackUrl}
            onBlurFunction={onBlurFunction}
          />
        </div>
        <div>
          {formatMessage({ id: 'flow.failure.alarm.mail' })}
          <LineInput
            updateGraph={updateGraph}
            attr="callbackEmail"
            value={flowAttributes.callbackEmail}
            onBlurFunction={onBlurFunction}
          />
        </div>
        <Button style={{ marginTop: '10px' }} onClick={() => setSlaModalShow(true)}>
          {formatMessage({ id: 'flow.autokill.settings' })}
        </Button>
      </div>

      <Modal
        title={formatMessage({ id: 'flow.autokill.settings' })}
        visible={slaModalShow}
        onCancel={() => setSlaModalShow(false)}
        onOk={() => {
          if (dispatch) {
            dispatch({
              type: 'flowEditor/changeFlowAttributes',
              payload: {
                slaDuration: getDaySeconds(slaTime),
                slaEmail,
                slaKill: true,
              },
              callback: () => setSlaModalShow(false),
            });
          }
        }}
      >
        <Form layout="vertical">
          <Form.Item label={formatMessage({ id: 'flow.duration' })} required>
            <TimePicker value={slaTime} onChange={t => setSlaTime(t)} />
          </Form.Item>
          <Form.Item label="SLA Email">
            <Input
              type="textarea"
              value={slaEmail}
              onChange={t => setSlaEmail(t.currentTarget.value)}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default connect(({ flowEditor }: ConnectState) => ({
  ...flowEditor,
}))(DetailPanel);
