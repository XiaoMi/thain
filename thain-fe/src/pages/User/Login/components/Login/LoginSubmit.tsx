/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import classNames from 'classnames';
import { Button, Form } from 'antd';
import styles from './index.less';
import { ButtonProps } from 'antd/lib/button';
const FormItem = Form.Item;

interface LoginSubmitProps extends ButtonProps {
  className?: string;
}

const LoginSubmit: React.SFC<LoginSubmitProps> = ({ className, ...rest }) => {
  const clsString = classNames(styles.submit, className);
  return (
    <FormItem>
      <Button size="large" className={clsString} type="primary" htmlType="submit" {...rest} />
    </FormItem>
  );
};

export default LoginSubmit;
