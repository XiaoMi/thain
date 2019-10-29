import React, { useState } from 'react';
import ConnectState, { ConnectProps } from '@/models/connect';
import { X5ConfigModel } from './models/X5ConfigModel';
import { formatMessage } from 'umi-plugin-locale';
import ButtonGroup from 'antd/es/button/button-group';
import { Button, Table, Modal, Form, Input, message } from 'antd';
import { connect } from 'dva';
import { TableResult } from '@/typings/ApiResult';
import { PaginationConfig } from 'antd/lib/table';
import X5Tag from './X5Tag';

interface Props extends ConnectProps {
  tableResult: TableResult<X5ConfigModel>;
  loading?: boolean;
}

const { Item } = Form;

const X5ConfigTable: React.FC<Props> = ({
  tableResult: { data, page, count, pageSize },
  dispatch,
}) => {
  const [appId, setAppId] = useState<string>();
  const [model, setModel] = useState<X5ConfigModel>(new X5ConfigModel());
  const [isVisiable, setIsVisiable] = useState(false);
  const [deleteVisiable, setDeleteVisiable] = useState(false);
  const [disable, setDisable] = useState(false);
  function handelDelete() {
    if (dispatch && appId) {
      dispatch({
        type: 'x5config/delete',
        payload: appId,
      });
      setDeleteVisiable(false);
    }
  }
  function tableChange(pagination: PaginationConfig) {
    if (dispatch) {
      dispatch({
        type: 'x5config/fetchTable',
        payload: {
          page: pagination.current,
          pageSize: pagination.pageSize,
        },
      });
    }
  }
  function handleOk() {
    if (
      model.principals.length === 0 ||
      model.appId === '' ||
      model.appKey === '' ||
      model.appName === ''
    ) {
      message.error('Please Complete X5Config Info', 0.5);
      return;
    }
    if (disable) {
      if (dispatch) {
        dispatch({
          type: 'x5config/update',
          payload: model,
        });
        setIsVisiable(false);
      }
    } else {
      if (dispatch) {
        dispatch({
          type: 'x5config/add',
          payload: model,
          callback: setIsVisiable,
        });
      }
    }
  }
  const column = [
    { title: formatMessage({ id: 'x5config.app.id' }), dataIndex: 'appId' },
    { title: formatMessage({ id: 'x5config.app.key' }), dataIndex: 'appKey' },
    { title: formatMessage({ id: 'x5config.app.name' }), dataIndex: 'appName' },
    {
      title: formatMessage({ id: 'x5config.app.principal' }),
      render(record: X5ConfigModel) {
        return (
          <>
            {record.principals.map((value: string, index: number) => {
              return <div key={index}>{value}</div>;
            })}
          </>
        );
      },
    },
    { title: formatMessage({ id: 'x5config.app.description' }), dataIndex: 'description' },
    { title: formatMessage({ id: 'x5config.app.create.time' }), dataIndex: 'createTime' },
    {
      title: formatMessage({ id: 'x5config.app.operation' }),
      render: (record: X5ConfigModel) => {
        return (
          <ButtonGroup>
            <Button
              onClick={() => {
                setIsVisiable(true);
                const modalArray: Array<string> = new Array();
                record.principals.forEach(element => {
                  modalArray.push(element);
                });
                setModel({
                  appId: record.appId,
                  appKey: record.appKey,
                  appName: record.appName,
                  principals: modalArray,
                  description: record.description,
                });
                setDisable(true);
              }}
            >
              {formatMessage({ id: 'x5config.app.edit.button' })}
            </Button>
            <Button
              onClick={() => {
                setDeleteVisiable(true);
                setAppId(record.appId);
              }}
            >
              {formatMessage({ id: 'x5config.app.delete.button' })}
            </Button>
          </ButtonGroup>
        );
      },
    },
  ];
  return (
    <>
      <Modal
        onCancel={() => {
          setIsVisiable(false);
        }}
        visible={isVisiable}
        destroyOnClose
        closable={false}
        onOk={handleOk}
      >
        <Form>
          <Item label={formatMessage({ id: 'x5config.app.id' })} required>
            <Input
              disabled={disable}
              placeholder="please input AppId"
              defaultValue={model.appId}
              onChange={e => {
                setModel({ ...model, appId: e.target.value });
              }}
            />
          </Item>
          <Item label={formatMessage({ id: 'x5config.app.key' })} required>
            <Input
              placeholder="please input AppKey"
              defaultValue={model.appKey}
              id="x5config.appName"
              onChange={e => {
                setModel({ ...model, appKey: e.target.value });
              }}
            />
          </Item>
          <Item label={formatMessage({ id: 'x5config.app.name' })} required>
            <Input
              placeholder="please input AppName"
              defaultValue={model.appName}
              onChange={e => {
                setModel({ ...model, appName: e.target.value });
              }}
            />
          </Item>
          <Item label={formatMessage({ id: 'x5config.app.principal' })} required>
            <X5Tag principals={model.principals} setTag={setModel} />
          </Item>
          <Item label={formatMessage({ id: 'x5config.app.description' })}>
            <Input
              placeholder="please input description"
              defaultValue={model.description}
              onChange={e => {
                setModel({ ...model, description: e.target.value });
              }}
            />
          </Item>
        </Form>
      </Modal>
      <Modal
        title={'delete info'}
        onOk={handelDelete}
        visible={deleteVisiable}
        onCancel={() => {
          setDeleteVisiable(false);
        }}
      >
        {'Delete message confirmation'}
      </Modal>
      <Button
        onClick={() => {
          setIsVisiable(true);
          setDisable(false);
          setModel(new X5ConfigModel());
        }}
        type="primary"
        style={{ float: 'right', marginBottom: '20px', zIndex: 99 }}
      >
        {formatMessage({ id: 'x5config.app.add.button' })}
      </Button>
      <Table
        columns={column}
        onChange={tableChange}
        dataSource={data}
        rowKey="appId"
        pagination={{
          current: page,
          pageSize: pageSize,
          total: count,
          showSizeChanger: true,
        }}
      />
    </>
  );
};
export default connect(({ x5config, loading }: ConnectState) => ({
  tableResult: x5config.tableResult,
  loading: loading.models.x5config,
}))(X5ConfigTable);
