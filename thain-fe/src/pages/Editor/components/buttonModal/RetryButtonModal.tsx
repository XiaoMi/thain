import React, { useState } from 'react';
import { Button, Modal, Form, InputNumber, Switch, notification } from 'antd';
import { formatMessage } from 'umi-plugin-locale';

interface Props {
  /**
   * 保存后触发, 返回是否保存成功
   */
  onSave: (setModelHide: () => void, timeInterval?: number, retryNumber?: number) => void;
  /**
   * 当前重试时间间隔，单位s
   */
  timeInterval?: number;
  /**
   * 当前重试次数
   */
  retryNumber?: number;
  style?: any;
}

/**
 * 设置重试的按钮 以及 模态框
 */
const RetryButtonModal: React.FC<Props> = ({
  onSave,
  style,
  timeInterval: initTimeInterval,
  retryNumber: initretryNumber,
}) => {
  const [modalShow, setModalShow] = useState(false);
  const [timeInterval, setTimeInterval] = useState(initTimeInterval);
  const [retryNumber, setretryNumber] = useState(initretryNumber);
  const [enableRetry, setEnableRetry] = useState(!!initTimeInterval && !!initretryNumber);

  return (
    <div>
      <Button style={style} onClick={() => setModalShow(true)}>
        {formatMessage({ id: 'editor.retry.settings' })}
      </Button>
      <Modal
        title={formatMessage({ id: 'editor.retry.settings' })}
        visible={modalShow}
        onCancel={() => setModalShow(false)}
        onOk={() => {
          if (enableRetry) {
            if (timeInterval && retryNumber) {
              onSave(() => setModalShow(false), timeInterval, retryNumber);
            } else {
              notification.warn({ message: 'Time interval and retry numbers are all required.' });
            }
          } else {
            onSave(() => setModalShow(false), undefined, undefined);
          }
        }}
      >
        <Form layout="vertical">
          <Form.Item
            label={formatMessage({ id: 'editor.retry.is.the.retry.mechanism.enabled' })}
            required
          >
            <Switch
              checked={enableRetry}
              onChange={checked => {
                setEnableRetry(checked);
              }}
            />
          </Form.Item>
          {enableRetry && (
            <>
              <Form.Item label={formatMessage({ id: 'editor.retry.numbers' })} required>
                <InputNumber value={retryNumber} onChange={setretryNumber} />
              </Form.Item>
              <Form.Item label={formatMessage({ id: 'editor.retry.time.interval' })} required>
                <InputNumber value={timeInterval} onChange={setTimeInterval} />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default RetryButtonModal;
