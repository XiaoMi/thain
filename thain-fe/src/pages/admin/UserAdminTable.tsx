/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { connect } from 'dva';
import { Button, Modal, Table } from 'antd';
import { TableResult } from '@/typings/ApiResult';
import { UserModel } from './model';
import { ConnectProps, ConnectState } from '@/models/connect';
import React, { useState } from 'react';
import ButtonGroup from 'antd/es/button/button-group';
import AddUserForm from './AddUserForm';
import { PaginationConfig } from 'antd/lib/table';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps {
  tableResult: TableResult<UserModel>;
  loading?: boolean;
}

const UserAdminTable: React.FC<Props> = ({ tableResult, dispatch, loading }) => {
  const { data, count, page, pageSize } = tableResult;
  const [isVisiable, setIsvisiable] = useState<boolean>(false);
  const [deleteId, setDeleteId] = useState();
  const columuns = [
    { dataIndex: 'userId', title: formatMessage({ id: 'admin.user.userId' }) },
    { dataIndex: 'userName', title: formatMessage({ id: 'admin.user.userName' }) },
    { dataIndex: 'email', title: formatMessage({ id: 'admin.user.email' }) },
    {
      dataIndex: 'admin',
      title: formatMessage({ id: 'admin.user.admin' }),
      render(admin: boolean) {
        if (admin) {
          return <div>{formatMessage({ id: 'admin.user.admin.yes' })}</div>;
        } else {
          return <div>{formatMessage({ id: 'admin.user.admin.no' })}</div>;
        }
      },
    },
    {
      title: formatMessage({ id: 'admin.user.operation' }),
      render(text: any, record: UserModel, index: number) {
        return (
          <ButtonGroup>
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
        {'do you want to delete this user?'}
      </Modal>
    </>
  );
};

export default connect(({ admin, loading }: ConnectState) => ({
  tableResult: admin.tableResult,
  loading: loading.models.admin,
}))(UserAdminTable);
