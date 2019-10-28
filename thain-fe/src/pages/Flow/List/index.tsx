/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import React, { useEffect } from 'react';
import FlowTable from './FlowTable';
import SearchForm from './SearchForm';
import { connect } from 'dva';
import { ConnectProps } from '@/models/connect';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps<{ flowId: number }> {}
const FlowList: React.FC<Props> = ({ dispatch }) => {
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'flowList/fetchTable',
      });
    }
    return () => {
      if (dispatch) {
        dispatch({
          type: 'flowList/unmount',
        });
      }
    };
  });
  return (
    <PageHeaderWrapper title={formatMessage({ id: 'flow.management' })}>
      <Card bordered={false}>
        <SearchForm />
        <div style={{ marginTop: '20px', overflow: '320px' }}>
          <FlowTable />
        </div>
      </Card>
    </PageHeaderWrapper>
  );
};

export default connect(() => ({}))(FlowList);
