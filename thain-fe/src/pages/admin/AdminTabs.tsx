/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { Tabs } from 'antd';
import { connect } from 'dva';
import React from 'react';
import { formatMessage } from 'umi-plugin-react/locale';
import UserAdminTable from './UserAdminTable';

const AdminTabs: React.FC = () => {
  const { TabPane } = Tabs;
  return (
    <div>
      <Tabs defaultActiveKey={'1'} tabPosition={'left'}>
        <TabPane tab={formatMessage({ id: 'admin.index.userAdmin' })} key={'1'}>
          <UserAdminTable />
        </TabPane>
        <TabPane tab={formatMessage({ id: 'admin.index.ClientAdmin' })} key={'2'} />
      </Tabs>
    </div>
  );
};

export default connect(() => ({}))(AdminTabs);
