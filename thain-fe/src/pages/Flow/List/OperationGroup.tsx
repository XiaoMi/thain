import ButtonGroup from 'antd/lib/button/button-group';
import TextArea from 'antd/lib/input/TextArea';
import { Button, Dropdown, Menu, Icon, Popconfirm, Modal, notification } from 'antd';
import { formatMessage } from 'umi-plugin-locale';
import { router } from 'umi';
import React, { useState } from 'react';
import { useDispatch } from 'dva';
import { FlowModel } from '@/commonModels/FlowModel';
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { FlowSearch } from './model';

interface Props {
  condition: FlowSearch;
  flow: FlowModel;
}

const OperationGroup: React.FC<Props> = ({ condition, flow }) => {
  const dispatch = useDispatch();
  const [variablesModalVisible, setVariablesModalVisible] = useState(false);
  const [variables, setVariables] = useState('');
  function renderButton() {
    switch (flow.schedulingStatus) {
      case FlowSchedulingStatus.NOT_SET:
        return (
          <Button
            onClick={() => {
              router.push(`/flow-editor/${flow.id}`);
            }}
          >
            {formatMessage({ id: 'flow.set.schedule' })}
          </Button>
        );
      case FlowSchedulingStatus.PAUSE:
        return (
          <Button
            onClick={() => {
              dispatch({
                type: 'flowList/scheduling',
                payload: {
                  id: flow.id,
                  condition,
                },
              });
            }}
          >
            {formatMessage({ id: 'flow.begin.schedule' })}
          </Button>
        );
      case FlowSchedulingStatus.SCHEDULING:
        return (
          <Button
            onClick={() => {
              dispatch({
                type: 'flowList/pause',
                payload: {
                  id: flow.id,
                  condition,
                },
              });
            }}
          >
            {formatMessage({ id: 'flow.pause.schedule' })}
          </Button>
        );
      default:
        return <div />;
    }
  }

  return (
    <div>
      <Modal
        title={formatMessage({ id: 'flow.fire.with.variables' })}
        visible={variablesModalVisible}
        okText={formatMessage({ id: 'flow.fire' })}
        onOk={() => {
          if (variables.split('\n').find(row => row.indexOf('=') === -1)) {
            notification.error({ message: 'variables error' });
            return;
          }
          dispatch({
            type: 'flowList/start',
            payload: {
              id: flow.id,
              condition,
              variables: variables
                .split('\n')
                .map(row => row.split('='))
                .reduce((o, t) => {
                  const [key, value] = t;
                  return { ...o, [key]: value };
                }, {}),
            },
          });
        }}
        onCancel={() => setVariablesModalVisible(false)}
      >
        {formatMessage({ id: 'flow.fire.with.variables.tips' })}
        <TextArea rows={4} value={variables} onChange={e => setVariables(e.target.value)} />
      </Modal>
      <ButtonGroup style={{ marginRight: '5px' }}>
        <Button
          onClick={() => {
            dispatch({
              type: 'flowList/start',
              payload: {
                id: flow.id,
                condition,
                variables: {},
              },
            });
          }}
        >
          {formatMessage({ id: 'flow.fire' })}
        </Button>
        <Dropdown
          overlay={
            <Menu onClick={() => setVariablesModalVisible(true)}>
              <Menu.Item>{formatMessage({ id: 'flow.fire.with.variables' })}</Menu.Item>
            </Menu>
          }
        >
          <Button>
            <Icon type="down" />
          </Button>
        </Dropdown>
      </ButtonGroup>
      <ButtonGroup>
        {renderButton()}
        <Button
          onClick={() => {
            router.push(`/flow-execution/list/${flow.id}`);
          }}
        >
          {formatMessage({ id: 'flow.view.log' })}
        </Button>
        <Button
          onClick={() => {
            router.push(`/flow-editor/${flow.id}`);
          }}
        >
          {formatMessage({ id: 'flow.edit' })}
        </Button>
        <Popconfirm
          title={formatMessage({ id: 'flow.delete.tips' })}
          onConfirm={() => {
            dispatch({
              type: 'flowList/delete',
              payload: {
                id: flow.id,
                condition,
              },
            });
          }}
          okText={formatMessage({ id: 'flow.delete' })}
          cancelText={formatMessage({ id: 'flow.cancel' })}
        >
          <Button type="danger">{formatMessage({ id: 'flow.delete' })}</Button>
        </Popconfirm>
      </ButtonGroup>
    </div>
  );
};

export default OperationGroup;
