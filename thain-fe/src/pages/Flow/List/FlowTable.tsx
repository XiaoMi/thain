/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { Button, Col, Dropdown, Icon, Menu, notification, Row, Table, Tooltip } from 'antd';
import React, { useState } from 'react';
import { ConnectProps, ConnectState } from '@/models/connect';
import { connect } from 'dva';
import { TableResult } from '@/typings/ApiResult';
import { FlowLastRunStatus } from '@/enums/FlowLastRunStatus';
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { FlowModel } from '@/commonModels/FlowModel';
import { PaginationConfig, SorterResult } from 'antd/lib/table';
import { ClickParam } from 'antd/es/menu';
import { formatMessage } from 'umi-plugin-react/locale';
import { FlowSearch } from './model';
import OperationGroup from './OperationGroup';

interface Props extends ConnectProps<{ flowId: number }> {
  tableResult?: TableResult<FlowModel>;
  condition: FlowSearch;
  loading: boolean;
  setCondition: Function;
  modelChange: Function;
}

const FlowTable: React.FC<Props> = ({
  tableResult: tableResultTmp,
  loading,
  dispatch,
  condition,
  setCondition,
  modelChange,
}) => {
  let tableResult = tableResultTmp;
  if (tableResult === undefined) {
    tableResult = new TableResult();
  }
  const { data, count, page, pageSize } = tableResult;
  const [batchId, setBatchId] = useState<number[] | string[]>([]);

  function initSorterIndex(): [string, string] {
    if (condition.sortKey) {
      if (condition.sortKey === 'id') {
        if (condition.sortOrderDesc === true) {
          return ['descend', ''];
        }
        if (condition.sortOrderDesc === false) {
          return ['ascend', ''];
        }
      }
      if (condition.sortKey === 'updateTime') {
        if (condition.sortOrderDesc === true) {
          return ['', 'descend'];
        }
        if (condition.sortOrderDesc === false) {
          return ['', 'ascend'];
        }
      }
    }
    return ['', ''];
  }

  const sorterIndex: [string, string] = initSorterIndex();
  function tableChange(pagination: PaginationConfig, sorter: SorterResult<any>) {
    const sort = sorter.order && {
      sortKey: sorter.columnKey,
      sortOrderDesc: sorter.order === 'descend',
    };
    const requestParam = sort
      ? {
          ...condition,
          page: pagination.current,
          pageSize: pagination.pageSize,
          ...sort,
        }
      : {
          ...condition,
          sortKey: undefined,
          sortOrderDesc: undefined,
          page: pagination.current,
          pageSize: pagination.pageSize,
        };
    setCondition(requestParam);
    modelChange({ ...requestParam });
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      sorter: true,
      fixed: 'left',
      defaultSortOrder: sorterIndex[0],
    },
    {
      title: formatMessage({ id: 'flow.name' }),
      dataIndex: 'name',
      key: 'name',
      fixed: 'left',
      width: 350,
      render: (name: string) => {
        return (
          <Tooltip title={name}>
            <span
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '350px',
                display: 'inline-block',
              }}
            >
              {name}
            </span>
          </Tooltip>
        );
      },
    },
    { title: formatMessage({ id: 'flow.cron' }), dataIndex: 'cron', key: 'cron' },
    {
      title: formatMessage({ id: 'flow.last.status' }),
      dataIndex: 'lastRunStatus',
      key: 'lastRunStatus',
      render: (status: number) => FlowLastRunStatus[status],
    },
    {
      title: formatMessage({ id: 'flow.schedule.status' }),
      dataIndex: 'schedulingStatus',
      key: 'schedulingStatus',
      render: (status: number) => FlowSchedulingStatus[status],
    },
    {
      title: formatMessage({ id: 'flow.status.update.time' }),
      dataIndex: 'statusUpdateTime',
      key: 'status_update_time',
      sorter: true,
      defaultSortOrder: sorterIndex[1],
      render(time: number) {
        return new Date(time).toLocaleString();
      },
    },

    {
      title: formatMessage({ id: 'flow.create.user' }),
      dataIndex: 'createUser',
      key: 'createUser',
    },
    {
      title: 'App',
      dataIndex: 'createAppId',
      key: 'createAppId',
    },
    {
      title: formatMessage({ id: 'flow.operation' }),
      dataIndex: 'id',
      key: 'operation',
      fixed: 'right',
      render: (id: number, item: FlowModel) => {
        return <OperationGroup condition={condition} flow={item} />;
      },
    },
  ];
  function handleMenuClick(e: ClickParam) {
    if (dispatch) {
      switch (e.key) {
        case '1':
          notification.info({ message: `${formatMessage({ id: 'flow.batch.fire' })}:${batchId}` });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/start',
              payload: {
                id,
                condition,
              },
            });
          });
          break;
        case '2':
          notification.info({ message: `${formatMessage({ id: 'flow.batch.begin' })}:${batchId}` });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/scheduling',
              payload: {
                id,
                condition,
              },
            });
          });
          break;
        case '3':
          notification.info({ message: `${formatMessage({ id: 'flow.batch.pause' })}:${batchId}` });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/pause',
              payload: {
                id,
                condition,
              },
            });
          });
          break;
        case '4':
          notification.info({
            message: `${formatMessage({ id: 'flow.batch.delete' })}:${batchId}`,
          });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/delete',
              payload: {
                id,
                condition,
              },
            });
          });
          break;
        case '5':
          notification.info({ message: `${formatMessage({ id: 'flow.batch.kill' })}:${batchId}` });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/kill',
              payload: {
                id,
                condition,
              },
            });
          });
          break;
        default:
      }
      setBatchId([]);
    }
  }
  const menu = (
    <Menu onClick={handleMenuClick}>
      <Menu.Item key="1">
        <Icon type="play-circle" theme="twoTone" />
        {formatMessage({ id: 'flow.fire' })}
      </Menu.Item>
      <Menu.Item key="2">
        <Icon type="clock-circle" theme="twoTone" />
        {formatMessage({ id: 'flow.begin.schedule' })}
      </Menu.Item>
      <Menu.Item key="3">
        <Icon type="pause-circle" theme="twoTone" />
        {formatMessage({ id: 'flow.pause.schedule' })}
      </Menu.Item>
      <Menu.Item key="4">
        <Icon type="delete" theme="twoTone" />
        {formatMessage({ id: 'flow.delete' })}
      </Menu.Item>
      <Menu.Item key="5">
        <Icon type="stop" theme="twoTone" />
        {formatMessage({ id: 'flow.kill.schedule' })}
      </Menu.Item>
    </Menu>
  );
  const rowSelection = {
    selectedRowKeys: batchId,
    onChange: (selectedRowKeys: string[] | number[]) => {
      setBatchId(selectedRowKeys);
    },
  };
  return (
    <div>
      <div style={{ marginBottom: 10 }}>
        <Row>
          <Col xs={10} sm={8} md={6} lg={4} xl={2}>
            <a href="/flow-editor">
              <Button type="primary">{formatMessage({ id: 'flow.create' })}</Button>
            </a>
          </Col>
          {batchId.length > 0 ? (
            <Col xs={10} sm={8} md={6} lg={4} xl={2}>
              <Dropdown overlay={menu}>
                <Button>
                  {formatMessage({ id: 'flow.batch.operation' })} <Icon type="down" />
                </Button>
              </Dropdown>
            </Col>
          ) : (
            ''
          )}
        </Row>
      </div>

      <Table
        rowSelection={rowSelection}
        scroll={{ x: 'max-content' }}
        columns={columns}
        rowKey={(record: any) => record.id}
        dataSource={data}
        pagination={{
          showSizeChanger: true,
          pageSize,
          total: count,
          current: page,
        }}
        onChange={tableChange}
        loading={loading}
      />
    </div>
  );
};

export default connect(({ flowList, loading }: ConnectState) => ({
  tableResult: flowList.tableResult,
  loading: loading.models.flowList,
}))(FlowTable);
