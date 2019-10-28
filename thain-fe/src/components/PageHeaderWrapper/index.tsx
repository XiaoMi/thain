/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import { RouteContext } from '@ant-design/pro-layout';
import { PageHeader, Typography } from 'antd';
import styles from './index.less';

interface IPageHeaderWrapperProps {
  content?: React.ReactNode;
  title: React.ReactNode;
}

const PageHeaderWrapper: React.SFC<IPageHeaderWrapperProps> = ({
  children,
  title,
  content,
  ...restProps
}) => (
  <RouteContext.Consumer>
    {value => (
      <div style={{ margin: '-24px -24px 0' }}>
        <PageHeader
          {...value}
          title={
            <Typography.Title
              level={4}
              style={{
                margin: 0,
              }}
            >
              {title || value.title}
            </Typography.Title>
          }
          {...restProps}
        >
          {content}
        </PageHeader>
        {children ? <div className={styles.content}>{children}</div> : null}
      </div>
    )}
  </RouteContext.Consumer>
);

export default PageHeaderWrapper;
