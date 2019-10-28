/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { connect } from 'dva';
import React, { useState } from 'react';
import { Button, Form, Icon, Input, Modal, notification, Select } from 'antd';
import { UserModel } from './models/UserAdminModel';
import { ConnectProps } from '@/models/connect';
import { formatMessage } from 'umi-plugin-react/locale';
import { WrappedFormUtils } from 'antd/lib/form/Form';

interface Props extends ConnectProps {
  readonly form: WrappedFormUtils<UserModel>;
}

const addUserForm: React.FC<Props> = ({ form, dispatch }) => {
  const [isVisiable, setIsvisiable] = useState(false);
  const { Item } = Form;
  const { Option } = Select;
  const [isAdmin, setIsAdmin] = useState(false);
  const { getFieldDecorator, getFieldsValue, getFieldsError } = form;
  const handelSubmit = () => {
    const values = getFieldsValue();
    for (const key in values) {
      if (values[key] === undefined) {
        notification.open({
          message: 'Error Tip',
          description: 'Please complete the input information',
          duration: 1,
        });
        return;
      }
    }
    const addUserModel: UserModel = values;
    const errors = getFieldsError();
    for (const error in errors) {
      if (errors[error] !== undefined) return;
    }
    addUserModel.admin = isAdmin;
    //调用接口发请求
    if (dispatch) {
      dispatch({
        payload: addUserModel,
        type: 'admin/add',
        callBack: () => setIsvisiable(false),
      });
    }
  };
  return (
    <div>
      <Button
        type="primary"
        style={{ float: 'right', marginBottom: '20px', zIndex: 99 }}
        onClick={() => {
          setIsvisiable(true);
        }}
      >
        {formatMessage({ id: 'admin.user.addUser' })}
      </Button>
      <Modal
        title={formatMessage({ id: 'admin.user.info' })}
        visible={isVisiable}
        onCancel={() => {
          setIsvisiable(false);
        }}
        onOk={handelSubmit}
        closable={false}
        destroyOnClose
      >
        <Form>
          <Item label={formatMessage({ id: 'admin.user.userId' })}>
            {getFieldDecorator('userId', {
              rules: [{ required: true, message: 'Please input your userId!' }],
            })(
              <Input
                prefix={<Icon type="key" style={{ color: 'rgba(0,0,0,.25)' }} />}
                placeholder="Please input userId"
              />,
            )}
          </Item>
          <Item label={formatMessage({ id: 'admin.user.userName' })}>
            {getFieldDecorator('username', {
              rules: [{ required: true, message: 'Please input your userName!' }],
            })(
              <Input
                placeholder="Please input  username"
                prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />}
              />,
            )}
          </Item>
          <Item label={formatMessage({ id: 'admin.user.password' })}>
            {getFieldDecorator('password', {
              rules: [{ required: true, message: 'Please input your Password!' }],
            })(
              <Input
                prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
                type="password"
                placeholder=" please input Password"
              />,
            )}
          </Item>
          <Item label={formatMessage({ id: 'admin.user.email' })}>
            {getFieldDecorator('email', {
              rules: [{ required: true, message: 'Please input current email!', type: 'email' }],
            })(
              <Input
                prefix={<Icon type="global" style={{ color: 'rgba(0,0,0,.25)' }} />}
                type="email"
                placeholder="please input email "
              />,
            )}
          </Item>
        </Form>
        <div>{formatMessage({ id: 'admin.user.admin' })}</div>
        <Select
          defaultValue="false"
          style={{ width: 100 }}
          onChange={(value: string) => {
            let admin: boolean;
            value === 'false' ? (admin = false) : (admin = true);
            setIsAdmin(admin);
          }}
        >
          <Option value="true">{formatMessage({ id: 'admin.user.admin.yes' })}</Option>
          <Option value="false">{formatMessage({ id: 'admin.user.admin.no' })}</Option>
        </Select>
      </Modal>
    </div>
  );
};

const AddUserFrom = Form.create()(addUserForm);
export default connect()(AddUserFrom);
