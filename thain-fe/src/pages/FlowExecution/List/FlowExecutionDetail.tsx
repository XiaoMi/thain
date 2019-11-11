/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React, { useCallback, useState, useEffect } from 'react';
import { Row, Col, Button, Table, Icon } from 'antd';
import ConnectState from '@/models/connect';
import { useSelector, useDispatch } from 'dva';
import Editor from '@/pages/FlowEditor/editor';
import Logs from './Logs';
import { JobExecutionModel } from '@/commonModels/JobExecutionModel';
import { JobExecutionStatus } from '@/enums/JobExecutionStatus';
import { formatMessage } from 'umi-plugin-react/locale';

const height = window.innerHeight - 350;

function createPage(container: any): any {
  return new Editor.Flow({
    graph: { container, height },
    align: { grid: true },
  });
}

interface Props {
  flowExecutionId?: number;
}

const FlowExecutionDetail: React.FC<Props> = ({ flowExecutionId }) => {
  const dispatch = useDispatch();
  const flowExecutionDetail = useSelector((state: ConnectState) => state.flowExecutionDetail);
  const loading = useSelector((state: ConnectState) => state.loading.models.flowExecutionDetail);
  const { flowExecutionModel, jobExecutionModelList } = flowExecutionDetail;
  const [showLogs, setShowLogs] = useState(flowExecutionModel.logs);
  const [graph, setGraph] = useState();
  const [refresh, setRefresh] = useState(0);

  useEffect(() => {
    setShowLogs(flowExecutionModel.logs);
  }, [flowExecutionModel]);

  useEffect(() => {
    if (graph) {
      graph.on('afteritemunselected', (ev: any) => {
        setShowLogs(flowExecutionModel.logs);
      });
    }
  }, [graph, flowExecutionModel, refresh]);

  useEffect(() => {
    if (graph && flowExecutionId) {
      graph.changeAddEdgeModel({ shape: 'flow-smoot' });
      graph.on('afteritemselected', (ev: any) => {
        const model = ev.item.getModel();
        if (model.type === 'node') {
          setShowLogs(model.logs);
        }
      });
      dispatch({
        type: 'flowExecutionDetail/getGraph',
        payload: {
          flowExecutionId,
          graph,
        },
      });
    }
  }, [graph, flowExecutionId, refresh]);

  const graphContainer = useCallback(node => {
    if (!node) {
      return;
    }
    node.innerHTML = '';
    setGraph(createPage(node));
  }, []);

  const columns = [
    { title: 'Job Execution ID', dataIndex: 'id', key: 'id' },
    { title: 'Job name', dataIndex: 'name', key: 'name' },
    {
      title: 'status',
      dataIndex: 'status',
      key: 'statue',
      render: (status: number) => {
        return JobExecutionStatus[status];
      },
    },
    {
      title: 'createTime',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time: any) => {
        return new Date(time).toLocaleString();
      },
    },
    {
      title: 'updateTime',
      dataIndex: 'updateTime',
      key: 'updateTime',
      render: (time: any) => {
        return new Date(time).toLocaleString();
      },
    },
    {
      title: formatMessage({ id: 'flow.execution.consuming' }),
      dataIndex: 'updateTime',
      key: 'spendTime',
      render: (time: any, item: JobExecutionModel) => {
        return (item.updateTime - item.createTime) / 1000 + 's';
      },
    },
    {
      title: 'logs',
      dataIndex: 'name',
      key: 'logs',
      render: (name: any, item: JobExecutionModel) => {
        return (
          <Button
            onClick={() => {
              graph.setSelected(graph.getSelected(name, false));
              graph.setSelected(name, true);
            }}
          >
            {formatMessage({ id: 'flow.execution.view.log' })}
          </Button>
        );
      },
    },
  ];

  return (
    <div>
      <Row type="flex">
        <Col span={16}>
          <div ref={graphContainer} />
        </Col>
        <Col span={8}>
          <div
            style={{
              borderLeft: '1px solid #e6e9ed',
              height: `${height}px`,
              maxHeight: `${height}px`,
            }}
          >
            <Icon
              type="redo"
              style={{
                fontSize: '20px',
                position: 'absolute',
                right: '35px',
                top: '-63px',
              }}
              onClick={() => setRefresh(t => t + 1)}
            />
            <Logs logs={showLogs} maxHeight={`${height}px`} />
          </div>
        </Col>
      </Row>
      <Row style={{ margin: '5px 0 -30px 0' }}>
        <Table
          size="small"
          columns={columns}
          rowKey={record => `rowKey${record.id}`}
          dataSource={jobExecutionModelList.sort((o1, o2) => o1.createTime - o2.createTime)}
          loading={loading}
          pagination={{ pageSize: 3 }}
        />
      </Row>
    </div>
  );
};

export default FlowExecutionDetail;
