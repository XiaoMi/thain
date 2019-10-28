/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import {
  Button,
  Col,
  Dropdown,
  Icon,
  Menu,
  notification,
  Popconfirm,
  Row,
  Table,
  Tooltip,
} from 'antd';
import ButtonGroup from 'antd/lib/button/button-group';
import React, { useState } from 'react';
import { router } from 'umi';
import { ConnectProps, ConnectState } from '@/models/connect';
import { connect } from 'dva';
import { TableResult } from '@/typings/ApiResult';
import { FlowLastRunStatus } from '@/enums/FlowLastRunStatus';
import { FlowSchedulingStatus } from '@/enums/FlowSchedulingStatus';
import { FlowModel } from '@/commonModels/FlowModel';
import { PaginationConfig, SorterResult } from 'antd/lib/table';
import { ClickParam } from 'antd/es/menu';
import { formatMessage } from 'umi-plugin-react/locale';

interface Props extends ConnectProps<{ flowId: number }> {
  tableResult: TableResult<FlowModel>;
  loading: boolean;
}

const FlowTable: React.FC<Props> = ({
  tableResult: { data, count, page, pageSize },
  loading,
  dispatch,
}) => {
  const [batchId, setBatchId] = useState<number[] | string[]>([]);
  function tableChange(
    pagination: PaginationConfig,
    filters: Record<any, string[]>,
    sorter: SorterResult<any>,
  ) {
    const sort = sorter.columnKey && {
      key: sorter.columnKey,
      orderDesc: sorter.order === 'descend',
    };
    if (dispatch) {
      dispatch({
        type: 'flowList/fetchTable',
        payload: {
          page: pagination.current,
          pageSize: pagination.pageSize,
          sort,
        },
      });
    }
  }
  function renderButton(flow: FlowModel) {
    if (flow.schedulingStatus) {
      switch (flow.schedulingStatus) {
        case FlowSchedulingStatus.NOT_SET:
          return (
            <Button
              onClick={() => {
                router.push('/flow-editor/' + flow.id);
              }}
            >
              {formatMessage({ id: 'flow.set.schedule' })}
            </Button>
          );
        case FlowSchedulingStatus.PAUSE:
          return (
            <Button
              onClick={() => {
                if (dispatch) {
                  dispatch({
                    type: 'flowList/scheduling',
                    payload: {
                      id: flow.id,
                    },
                  });
                }
              }}
            >
              {formatMessage({ id: 'flow.begin.schedule' })}
            </Button>
          );
        case FlowSchedulingStatus.SCHEDULING:
          return (
            <Button
              onClick={() => {
                if (dispatch) {
                  dispatch({
                    type: 'flowList/pause',
                    payload: {
                      id: flow.id,
                    },
                  });
                }
              }}
            >
              {formatMessage({ id: 'flow.pause.schedule' })}
            </Button>
          );
        default:
          return <div />;
      }
    }
    return <div />;
  }
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', sorter: true, fixed: 'left' },
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
        return (
          <div>
            <ButtonGroup>
              <Button
                onClick={() => {
                  if (dispatch) {
                    dispatch({
                      type: 'flowList/start',
                      payload: { id },
                    });
                  }
                }}
              >
                {formatMessage({ id: 'flow.fire' })}
              </Button>
              {renderButton(item)}
              <Button
                onClick={() => {
                  router.push('/flow-execution/list/' + id);
                }}
              >
                {formatMessage({ id: 'flow.view.log' })}
              </Button>
              <Button
                onClick={() => {
                  router.push('/flow-editor/' + id);
                }}
              >
                {formatMessage({ id: 'flow.edit' })}
              </Button>
              <Popconfirm
                title={formatMessage({ id: 'flow.delete.tips' })}
                onConfirm={() => {
                  if (dispatch) {
                    dispatch({
                      type: 'flowList/delete',
                      payload: { id },
                    });
                  }
                }}
                okText={formatMessage({ id: 'flow.delete' })}
                cancelText={formatMessage({ id: 'flow.cancel' })}
              >
                <Button type="danger">{formatMessage({ id: 'flow.delete' })}</Button>
              </Popconfirm>
            </ButtonGroup>
          </div>
        );
      },
    },
  ];
  function handleMenuClick(e: ClickParam) {
    if (dispatch) {
      switch (e.key) {
        case '1':
          notification.info({ message: formatMessage({ id: 'flow.batch.fire' }) + batchId });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/start',
              payload: {
                id,
              },
            });
          });
          break;
        case '2':
          notification.info({ message: formatMessage({ id: 'flow.batch.begin' }) + batchId });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/scheduling',
              payload: {
                id,
              },
            });
          });
          break;
        case '3':
          notification.info({ message: formatMessage({ id: 'flow.batch.pause' }) + batchId });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/pause',
              payload: {
                id,
              },
            });
          });
          break;
        case '4':
          notification.info({ message: formatMessage({ id: 'flow.batch.delete' }) + batchId });
          batchId.forEach((id: number | string) => {
            dispatch({
              type: 'flowList/delete',
              payload: {
                id,
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
    </Menu>
  );
  const rowSelection = {
    selectedRowKeys: batchId,
    onChange: (selectedRowKeys: string[] | number[], selectedRows: FlowModel[]) => {
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
