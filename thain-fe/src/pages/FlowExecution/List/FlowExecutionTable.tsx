/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { useDispatch, useSelector } from 'dva';
import { ConnectState } from '@/models/connect';
import React, { useState } from 'react';
import { Button, Modal, Table } from 'antd';
import { FlowExecutionStatus, getScheduleStatusDesc } from '@/enums/FlowExecutionStatus';
import FlowExecutionModel from '@/commonModels/FlowExecutionModel';
import { formatMessage } from 'umi-plugin-react/locale';
import FlowExecutionDetail from './FlowExecutionDetail';
import styles from './table.less';

function FlowExecutionTable() {
  const dispatch = useDispatch();
  const flowExecutionList = useSelector((state: ConnectState) => state.flowExecutionList);
  const loading = useSelector((state: ConnectState) => state.loading.models.flowExecutionList);

  const { count, data, page, pageSize } = flowExecutionList;
  function tablePageChange(newPage: number, newPageSize?: number) {
    if (newPage !== page || newPageSize !== pageSize) {
      dispatch({
        type: 'flowExecutionList/fetchTable',
        payload: {
          ...flowExecutionList,
          page: newPage,
          pageSize: newPageSize || pageSize,
        },
      });
    }
  }

  const [showModel, setShowModel] = useState(false);
  const [flowExecutionId, setFlowExecutionId] = useState<number>();

  function killFlowExecution(id: number) {
    dispatch({
      type: 'flowExecutionList/killFlowExecution',
      payload: {
        flowExecutionId: id,
      },
    });
  }

  const renderRowClass = (record: FlowExecutionModel) => {
    switch (record.status) {
      case FlowExecutionStatus.WAITING:
        return styles.waitingRow;
      case FlowExecutionStatus.SUCCESS:
        return styles.successRow;
      case FlowExecutionStatus.ERROR:
        return styles.errorRow;
      case FlowExecutionStatus.RUNNING:
        return styles.runningRow;
      case FlowExecutionStatus.KILLED:
        return styles.killedRow;
      case FlowExecutionStatus.DO_NOT_RUN_SAME_TIME:
        return styles.doNotRunSameTimeRow;
      case FlowExecutionStatus.ERROR_WAITING_RETRY:
        return styles.errorWaitingRetry;
      default:
        return styles.successRow;
    }
  };

  const columns = [
    { title: 'id', dataIndex: 'id', key: 'id', align: 'center' },
    {
      title: formatMessage({ id: 'flow.execution.trigger.type' }),
      dataIndex: 'triggerType',
      key: 'triggerType',
      align: 'center',
      render: (triggerType: number) => {
        switch (triggerType) {
          case 1:
            return formatMessage({ id: 'flow.execution.manual' });
          case 2:
            return formatMessage({ id: 'flow.execution.auto' });
          case 3:
            return formatMessage({ id: 'flow.execution.retry' });
          default:
            return triggerType;
        }
      },
    },
    {
      title: formatMessage({ id: 'flow.execution.status' }),
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      render: (status: any) => {
        return getScheduleStatusDesc(status);
      },
    },
    {
      title: formatMessage({ id: 'flow.execution.execution.machine' }),
      dataIndex: 'hostInfo',
      key: 'hostInfo',
      align: 'center',
    },
    {
      title: formatMessage({ id: 'flow.execution.create.time' }),
      dataIndex: 'createTime',
      key: 'createTime',
      align: 'center',
      render: (time: any) => {
        return new Date(time).toLocaleString();
      },
    },
    {
      title: formatMessage({ id: 'flow.execution.update.time' }),
      dataIndex: 'updateTime',
      key: 'updateTime',
      align: 'center',
      render: (time: any) => {
        return new Date(time).toLocaleString();
      },
    },
    {
      title: formatMessage({ id: 'flow.execution.variables' }),
      dataIndex: 'variables',
      key: 'variables',
      align: 'center',
      render: (variables: any) => {
        return JSON.stringify(variables);
      },
    },
    {
      title: 'logs',
      dataIndex: 'id',
      key: 'logs',
      align: 'center',
      render: (id: any) => {
        return (
          <Button
            onClick={() => {
              setFlowExecutionId(id);
              setShowModel(true);
            }}
          >
            {formatMessage({ id: 'flow.execution.view.log' })}
          </Button>
        );
      },
    },
    {
      title: formatMessage({ id: 'flow.execution.operation' }),
      dataIndex: 'id',
      key: 'operation',
      align: 'center',
      render: (id: number, item: FlowExecutionModel) => {
        return (
          <Button
            disabled={FlowExecutionStatus[item.status] !== 'RUNNING'}
            onClick={() => killFlowExecution(id)}
          >
            kill
          </Button>
        );
      },
    },
  ];

  return (
    <div>
      <Table
        rowClassName={renderRowClass}
        columns={columns}
        rowKey={(record: FlowExecutionModel) => `rowKey${record.id}`}
        dataSource={data}
        pagination={{
          showSizeChanger: true,
          pageSize,
          total: count,
          current: page,
          onShowSizeChange: tablePageChange,
          onChange: tablePageChange,
        }}
        loading={loading}
      />
      <Modal
        style={{ top: 20 }}
        title={formatMessage({ id: 'flow.execution.log.detail' })}
        visible={showModel}
        width="70%"
        onCancel={() => setShowModel(false)}
        footer={false}
      >
        <FlowExecutionDetail flowExecutionId={flowExecutionId} />
      </Modal>
    </div>
  );
}

export default FlowExecutionTable;
