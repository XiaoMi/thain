/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import React, { useState } from 'react';
import FlowTable from './FlowTable';
import SearchForm from './SearchForm';
import { connect } from 'dva';
import { ConnectProps } from '@/models/connect';
import { formatMessage } from 'umi-plugin-react/locale';
import { FlowSearch } from './model';

interface Props extends ConnectProps<{ flowId: number }> {}
const FlowList: React.FC<Props> = () => {
  const [conditon, setCondition] = useState<FlowSearch>(initParam());
  function initParam() {
    const paramObject = new FlowSearch();
    const keys = [
      'flowId',
      'lastRunStatus',
      'flowName',
      'searchApp',
      'createUser',
      'scheduleStatus',
      'updateTime',
      'page',
      'pageSize',
      'sortKey',
      'sortOrderDesc',
    ];
    const url = window.location.search.substring(1);
    const params = url.split('&');
    for (const key of keys) {
      for (const param of params) {
        const entry = param.split('=');
        if (entry.length === 2 && key === entry[0]) {
          if (
            key === 'flowId' ||
            key === 'lastRunStatus' ||
            key === 'scheduleStatus' ||
            key === 'page' ||
            key === 'pageSize' ||
            key === 'sortOrderDesc'
          ) {
            const value = parseInt(entry[1], 10);
            if (!isNaN(value)) {
              switch (key) {
                case 'flowId':
                  paramObject.flowId = value;
                  break;
                case 'lastRunStatus':
                  paramObject.lastRunStatus = value;
                  break;
                case 'scheduleStatus':
                  paramObject.scheduleStatus = value;
                  break;
                case 'page':
                  paramObject.page = value;
                  break;
                case 'pageSize':
                  paramObject.pageSize = value;
                  break;
                case 'sortOrderDesc':
                  value === 1
                    ? (paramObject.sortOrderDesc = true)
                    : (paramObject.sortOrderDesc = false);
                  break;
                default:
              }
            }
          } else if (
            key === 'flowName' ||
            key === 'searchApp' ||
            key === 'createUser' ||
            key === 'sortKey'
          ) {
            switch (key) {
              case 'flowName':
                paramObject.flowName = entry[1];
                break;
              case 'searchApp':
                paramObject.searchApp = entry[1];
                break;
              case 'createUser':
                paramObject.createUser = entry[1];
                break;
              case 'sortKey':
                if (entry[0] === 'id' || entry[0] === 'updateTime') {
                  paramObject.sortKey = entry[0];
                }
                break;
              default:
            }
          } else if (key === 'updateTime') {
            const arrays = entry[1].substring(1, entry[1].length - 1);
            const date = arrays.split(',');
            if (
              date.length === 2 &&
              !isNaN(parseInt(date[0], 10)) &&
              !isNaN(parseInt(date[1], 10))
            ) {
              paramObject.updateTime = [...[parseInt(date[0], 10), parseInt(date[1], 10)]];
            }
          }
        }
      }
    }
    return paramObject;
  }

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'flow.management' })}>
      <Card bordered={false}>
        <SearchForm condition={conditon} setCondition={setCondition} />
        <div style={{ marginTop: '20px', overflow: '320px' }}>
          <FlowTable condition={conditon} setCondition={setCondition} />
        </div>
      </Card>
    </PageHeaderWrapper>
  );
};

export default connect(() => ({}))(FlowList);
