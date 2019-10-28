/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { formatMessage, FormattedMessage } from 'umi-plugin-react/locale';
import { Alert, Icon } from 'antd';
import LoginComponents from './components/Login';
import styles from './style.less';
import { Dispatch } from 'redux';
import { IStateType } from './model';
import { FormComponentProps } from 'antd/lib/form';

const { Tab, UserId, Password, Submit } = LoginComponents;

interface LoginProps {
  dispatch: Dispatch<any>;
  userLogin: IStateType;
  submitting: boolean;
}
interface LoginState {
  type: string;
}
export interface FromDataType {
  userId: string;
  password: string;
  mobile: string;
  captcha: string;
}

@connect(
  ({
    userLogin,
    loading,
  }: {
    userLogin: IStateType;
    loading: {
      effects: {
        [key: string]: string;
      };
    };
  }) => ({
    userLogin,
    submitting: loading.effects['userLogin/login'],
  }),
)
class Login extends Component<LoginProps, LoginState> {
  state: LoginState = {
    type: 'account',
  };
  loginForm: FormComponentProps['form'] | undefined | null;

  handleSubmit = (err: any, values: FromDataType) => {
    if (!err) {
      const { dispatch } = this.props;
      dispatch({
        type: 'userLogin/login',
        payload: {
          ...values,
        },
      });
    }
  };

  renderMessage = (content: string) => (
    <Alert style={{ marginBottom: 24 }} message={content} type="error" showIcon />
  );

  render() {
    const { userLogin, submitting } = this.props;
    const { status } = userLogin;
    const loginType = 'account';
    return (
      <div className={styles.main}>
        <LoginComponents
          defaultActiveKey={loginType}
          onSubmit={this.handleSubmit}
          ref={(form: any) => {
            this.loginForm = form;
          }}
        >
          <Tab key="account" tab={formatMessage({ id: 'user-login.login.tab-login-credentials' })}>
            {status === 'error' &&
              loginType === 'account' &&
              !submitting &&
              this.renderMessage(
                formatMessage({ id: 'user-login.login.message-invalid-credentials' }),
              )}
            <UserId
              name="userId"
              placeholder={formatMessage({ id: 'user-login.login.userId' })}
              rules={[
                {
                  required: true,
                  message: formatMessage({ id: 'user-login.userId.required' }),
                },
              ]}
            />
            <Password
              name="password"
              placeholder={formatMessage({ id: 'user-login.login.password' })}
              rules={[
                {
                  required: true,
                  message: formatMessage({ id: 'user-login.password.required' }),
                },
              ]}
              onPressEnter={() =>
                this.loginForm && this.loginForm.validateFields(this.handleSubmit)
              }
            />
          </Tab>
          <Submit loading={submitting}>
            <FormattedMessage id="user-login.login.login" />
          </Submit>
          <div className={styles.other}>
            <FormattedMessage id="user-login.login.sign-in-with" />
            <a href={'/api/oauth2/authorization/google'}>
              <Icon type="google" className={styles.icon} theme="outlined" />
            </a>
          </div>
        </LoginComponents>
      </div>
    );
  }
}

export default Login;
