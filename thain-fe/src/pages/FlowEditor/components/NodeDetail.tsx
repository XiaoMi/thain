/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 * @date 2019年03月27日
 * @author liangyongrui@xiaomi.com
 */
import React from 'react';
import { useSelector } from 'dva';
import LineInput from './input/LineInput';
import TextareaInput from './input/TextareaInput';
import { Form } from 'antd';
import RichTextInput from './input/RichTextInput';
import SelectInput from './input/SelectInput';
import SqlInput from './input/SqlInput';
import ShellInput from './input/ShellInput';
import UploadBase64Input from './input/UploadBase64Input';
import { formatMessage } from 'umi-plugin-react/locale';
import { ConnectState } from '@/models/connect';

const getInput = (inputName: string) => {
  switch (inputName) {
    case 'line':
      return LineInput;
    case 'richText':
      return RichTextInput;
    case 'select':
      return SelectInput;
    case 'sql':
      return SqlInput;
    case 'shell':
      return ShellInput;
    case 'textarea':
      return TextareaInput;
    case 'uploadBase64':
      return UploadBase64Input;
    default:
      return LineInput;
  }
};

const NodeDetail: React.FC<{}> = () => {
  const flowEditor = useSelector((s: ConnectState) => s.flowEditor);
  const { selectedModel, componentDefines, updateGraph } = flowEditor;
  const { category, id } = selectedModel;
  const componentDefine = componentDefines.find(t => category === `${t.group}::${t.name}`);

  if (!category || updateGraph === undefined || !componentDefine) {
    return <div />;
  }

  const otherDetail: JSX.Element[] = [];
  componentDefine.items.forEach(item => {
    const Input = getInput(item.input.id);
    otherDetail.push(
      <Form.Item
        key={item.property + id}
        required={item.required}
        label={item.label || item.property}
      >
        <Input
          updateGraph={updateGraph}
          attr={item.property}
          value={selectedModel.attributes[item.property]}
          updateAttributes
          selectList={
            (item.input as {
              id: string;
              options?: Array<{
                id: string;
                name?: string;
              }>;
            }).options
          }
        />
      </Form.Item>,
    );
  });

  const formItemLayout = {
    labelCol: {
      xs: { span: 24 },
      sm: { span: 8 },
    },
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 16 },
    },
  };
  return (
    <Form {...formItemLayout}>
      <Form.Item label={formatMessage({ id: 'flow.component' })}>{category}</Form.Item>
      <Form.Item label={formatMessage({ id: 'flow.node.name' })} required>
        <LineInput
          updateGraph={updateGraph}
          attr="label"
          value={(() => {
            const begin = selectedModel.label.indexOf('::');
            if (begin === -1) {
              return selectedModel.label;
            }
            return selectedModel.label.substr(begin + 2);
          })()}
        />
      </Form.Item>
      <Form.Item label={formatMessage({ id: 'flow.status.callback.url' })}>
        <TextareaInput
          updateGraph={updateGraph}
          attr="callbackUrl"
          value={selectedModel.callbackUrl}
        />
      </Form.Item>
      <Form.Item label={formatMessage({ id: 'flow.condition' })} style={{ display: 'none' }}>
        <TextareaInput updateGraph={updateGraph} attr="condition" value={selectedModel.condition} />
      </Form.Item>
      {otherDetail}
    </Form>
  );
};
export default NodeDetail;
