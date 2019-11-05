/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { connect } from 'dva';
import { Button, Modal, Table, Select, Form } from 'antd';
import { TableResult } from '@/typings/ApiResult';
import { UserModel } from './models/UserAdminModel';
import { ConnectProps, ConnectState } from '@/models/connect';
import React, { useState, useEffect } from 'react';
import ButtonGroup from 'antd/es/button/button-group';
import AddUserForm from './AddUserForm';
import { PaginationConfig } from 'antd/lib/table';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  tableResult?: TableResult<UserModel>;
  loading?: boolean;
}

const UserAdminTable: React.FC<Props> = ({ tableResult, dispatch, loading }) => {
  if (tableResult === undefined) {
    tableResult = new TableResult();
  }
  const { data, count, page, pageSize } = tableResult;
  const [isVisiable, setIsvisiable] = useState<boolean>(false);
  const [deleteId, setDeleteId] = useState();
  const { Option } = Select;
  const [model, setModel] = useState({ ...new UserModel() });
  const [editVisiable, setEditVisiable] = useState(false);
  const { Item } = Form;
  const [defaultValue, setDefaultValue] = useState('false');
  const columuns = [
    { dataIndex: 'userId', title: formatMessage({ id: 'admin.user.userId' }) },
    { dataIndex: 'userName', title: formatMessage({ id: 'admin.user.userName' }) },
    { dataIndex: 'email', title: formatMessage({ id: 'admin.user.email' }) },
    {
      dataIndex: 'admin',
      title: formatMessage({ id: 'admin.user.admin' }),
      render(text: boolean, record: UserModel, index: number) {
        if (record.admin) {
          return <>{formatMessage({ id: 'admin.user.admin.yes' })}</>;
        } else {
          return <>{formatMessage({ id: 'admin.user.admin.no' })}</>;
        }
      },
    },
    {
      title: formatMessage({ id: 'admin.user.operation' }),
      render(text: string, record: UserModel, index: number) {
        return (
          <ButtonGroup>
            <Button
              onClick={() => {
                setModel({ ...new UserModel(), userId: record.userId });
                setEditVisiable(true);
                record.admin === true ? setDefaultValue('true') : setDefaultValue('false');
              }}
            >
              {formatMessage({ id: 'admin.user.edit' })}
            </Button>
            <Button
              onClick={() => {
                setDeleteId(record.userId);
                setIsvisiable(true);
              }}
            >
              {formatMessage({ id: 'admin.user.delete' })}
            </Button>
          </ButtonGroup>
        );
      },
    },
  ];

  useEffect(() => {
    return () => {
      if (dispatch) {
        dispatch({
          type: 'admin/unmount',
        });
      }
    };
  }, []);

  function tableChange(pagination: PaginationConfig) {
    if (dispatch) {
      dispatch({
        type: 'admin/fetchTable',
        payload: {
          pageSize: pagination.pageSize,
          page: pagination.current,
        },
      });
    }
  }

  function deleteUser() {
    if (dispatch) {
      dispatch({
        type: 'admin/delete',
        payload: {
          userId: deleteId,
        },
      });
    }
  }

  function updateUser() {
    if (dispatch) {
      dispatch({
        type: 'admin/update',
        payload: model,
        callback: () => {
          setEditVisiable(false);
        },
      });
    }
  }
  return (
    <>
      <AddUserForm />
      <Table
        dataSource={data}
        onChange={tableChange}
        rowKey="userId"
        columns={columuns}
        loading={loading}
        pagination={{
          showSizeChanger: true,
          pageSize: pageSize,
          total: count,
          current: page,
        }}
      />
      <Modal
        visible={isVisiable}
        onCancel={() => {
          setIsvisiable(false);
        }}
        closable={false}
        onOk={() => {
          deleteUser();
          setIsvisiable(false);
        }}
      >
        'do you want to delete this user?'
      </Modal>
      <Modal
        destroyOnClose
        closable={false}
        visible={editVisiable}
        onCancel={() => {
          setEditVisiable(false);
        }}
        onOk={updateUser}
      >
        <Form>
          <Item label={formatMessage({ id: 'admin.user.admin' })}>
            <Select<string>
              defaultValue={defaultValue}
              onChange={value => {
                let admin: boolean;
                value === 'false' ? (admin = false) : (admin = true);
                setModel({ ...model, admin: admin });
              }}
            >
              <Option value="true">{formatMessage({ id: 'admin.user.admin.yes' })}</Option>
              <Option value="false">{formatMessage({ id: 'admin.user.admin.no' })}</Option>
            </Select>
          </Item>
        </Form>
      </Modal>
    </>
  );
};

export default connect(({ admin, loading }: ConnectState) => ({
  tableResult: admin.tableResult,
  loading: loading.models.admin,
}))(UserAdminTable);
