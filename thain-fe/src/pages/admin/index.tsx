/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { useEffect } from 'react';
import { connect } from 'dva';
import AdminTabs from './AdminTabs';
import { ConnectProps } from '@/models/connect';
import AdminUserModelType from './models/UserAdminModel';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import { formatMessage } from 'umi-plugin-react/locale';
interface Prpos extends ConnectProps {}
const Admin: React.FC<Prpos> = ({ dispatch }) => {
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'admin/fetchTable',
      });
    }
    return () => {
      if (dispatch) {
        dispatch({
          type: 'admin/unmount',
        });
      }
    };
  }, [AdminUserModelType]);

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'admin.home.index' })}>
      <Card bordered={false}>
        <AdminTabs />
      </Card>
    </PageHeaderWrapper>
  );
};
export default connect()(Admin);
