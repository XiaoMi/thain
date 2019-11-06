/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import React, { useState, useEffect } from 'react';
import FlowTable from './FlowTable';
import SearchForm from './SearchForm';
import { connect } from 'dva';
import { ConnectProps } from '@/models/connect';
import { formatMessage } from 'umi-plugin-react/locale';
import { FlowSearch } from './model';
import { parse } from 'querystring';
import { router } from 'umi';
import { stringify } from 'querystring';
interface Props extends ConnectProps<{ flowId: number }> {}
const FlowList: React.FC<Props> = ({ dispatch }) => {
  const [conditon, setCondition] = useState<FlowSearch>(initParam());
  function initParam() {
    const createObject = new FlowSearch();
    const paramObject = parse(window.location.search.substring(1));
    const objectKeys = Object.entries(paramObject);
    for (const [index, value] of objectKeys) {
      if (value === '') {
        delete paramObject[index];
        continue;
      }
      if (
        index === 'flowId' ||
        index === 'lastRunStatus' ||
        index === 'scheduleStatus' ||
        index === 'page' ||
        index === 'pageSize'
      ) {
        const parseValue = parseInt(value as string, 10);
        if (!isNaN(parseValue)) {
          switch (index) {
            case 'flowId':
              createObject.flowId = parseValue;
              break;
            case 'lastRunStatus':
              createObject.lastRunStatus = parseValue;
              break;
            case 'scheduleStatus':
              createObject.scheduleStatus = parseValue;
              break;
            case 'page':
              createObject.page = parseValue;
              break;
            case 'pageSize':
              createObject.pageSize = parseValue;
              break;
            default:
              break;
          }
        }
      } else if (index === 'sortOrderDesc' && (value === 'true' || value === 'false')) {
        if (value === 'true') {
          createObject.sortOrderDesc = true;
        } else if (value === 'false') {
          createObject.sortOrderDesc = false;
        }
      } else {
        switch (index) {
          case 'flowName':
            createObject.flowName = value as string;
            break;
          case 'searchApp':
            createObject.searchApp = value as string;
            break;
          case 'createUser':
            createObject.createUser = value as string;
            break;
          case 'updateTime':
            if (!(value[0] === '' || value[1] === '')) {
              createObject.updateTime = [parseInt(value[0], 10), parseInt(value[1], 10)];
            }
            break;
          case 'sortKey':
            createObject.sortKey = value as string;
            break;
          default:
            break;
        }
      }
    }
    return createObject;
  }

  useEffect(() => {
    router.push(`/flow/list?${stringify(conditon as any)}`);
    const requestParam = Object.keys(conditon)
      .filter(t => conditon[t] !== '')
      .reduce((p, c) => ({ ...p, [c]: conditon[c] }), {});
    if (dispatch) {
      dispatch({
        type: 'flowList/fetchTable',
        payload: {
          ...requestParam,
        },
      });
    }
    return () => {
      if (dispatch) {
        dispatch({
          type: 'flowList/unmount',
        });
      }
    };
  }, [conditon]);

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
