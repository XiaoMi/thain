/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import { connect } from 'dva';
import { ConnectProps } from '@/models/connect';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import { formatMessage } from 'umi-plugin-react/locale';
import AdminTabs from './AdminTabs';

interface Prpos extends ConnectProps<{ type: string }> {}
const Admin: React.FC<Prpos> = ({ dispatch, computedMatch }) => {
  let type;
  if (computedMatch) {
    type = computedMatch.params.type;
  } else {
    type = 'user';
  }

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'admin.home.index' })}>
      <Card bordered={false}>
        <AdminTabs type={type} />
      </Card>
    </PageHeaderWrapper>
  );
};
export default connect()(Admin);
