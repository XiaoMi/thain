/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { extend } from 'umi-request';
import { notification } from 'antd';
import { ApiResult } from '@/typings/ApiResult';
import { stringify } from 'querystring';
import { formatMessage } from 'umi-plugin-react/locale';

interface ResponseError<D = any> extends Error {
  name: string;
  data: D;
  response: Response;
}

const codeMessage = {
  200: formatMessage({ id: '200' }),
  201: formatMessage({ id: '201' }),
  202: formatMessage({ id: '202' }),
  204: formatMessage({ id: '204' }),
  400: formatMessage({ id: '400' }),
  401: formatMessage({ id: '401' }),
  403: formatMessage({ id: '403' }),
  404: formatMessage({ id: '404' }),
  406: formatMessage({ id: '406' }),
  410: formatMessage({ id: '410' }),
  422: formatMessage({ id: '422' }),
  500: formatMessage({ id: '500' }),
  502: formatMessage({ id: '502' }),
  503: formatMessage({ id: '503' }),
  504: formatMessage({ id: '504' }),
};

/**
 * 异常处理程序
 */
const errorHandler = (error: ResponseError) => {
  const { response = {} as Response } = error;
  const errortext = codeMessage[response.status] || response.statusText;
  const { status, url } = response;

  notification.error({
    message: `请求错误 ${status}: ${url}`,
    description: errortext,
  });
};

/**
 * 配置request请求时的默认参数
 */
const request = extend({
  errorHandler, // 默认错误处理
  credentials: 'include', // 默认请求是否带上cookie
});

export default request;

function statusHandler(status: number, message: string) {
  if (status !== 200) {
    notification.error({
      message: status + ': ' + message,
    });
    return true;
  }
  return false;
}

export async function postForm<T = {}>(url: string, data?: any) {
  const res: ApiResult<T> | undefined = await request(url, {
    method: 'POST',
    requestType: 'form',
    data,
  });
  return parseResult(res);
}

export async function post<T = {}>(url: string, data?: any): Promise<T | undefined> {
  const res: ApiResult<T> | undefined = await request(url, { method: 'POST', data });
  return parseResult(res);
}

export async function del<T = {}>(url: string, data?: any): Promise<T | undefined> {
  const res: ApiResult<T> | undefined = await request(url, { method: 'DELETE', data });
  return parseResult(res);
}

export async function get<T = {}, U = {}>(url: string, data?: U): Promise<T | undefined> {
  let res: ApiResult<T> | undefined;
  if (data === undefined) {
    res = await request(url, {});
  } else {
    const newData = Object.keys(data)
      .filter(t => data[t] !== undefined && data[t] !== null)
      .reduce((p, c) => ({ ...p, [c]: data[c] }), {});
    res = await request(`${url}?${stringify(newData)}`, {});
  }
  return parseResult(res);
}

export async function patch<T = {}>(url: string, data?: any): Promise<T | undefined> {
  const res: ApiResult<T> | undefined = await request(url, { method: 'PATCH', data });
  return parseResult(res);
}

function parseResult<T = {}>(res?: ApiResult<T>) {
  if (!res || statusHandler(res.status, res.message)) {
    return undefined;
  }
  return res.data;
}
