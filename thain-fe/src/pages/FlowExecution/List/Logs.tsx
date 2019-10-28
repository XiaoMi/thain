/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import React from 'react';

type LogLevel = 'DEBUG' | 'INFO' | 'WARN' | 'ERROR';

export class LogEntity {
  /**
   * 毫秒时间戳
   */
  timestamp: number;

  /**
   * 日志等级
   */
  level: LogLevel;

  /**
   * 具体内容
   */
  content: string;

  constructor(timestamp: number, level: LogLevel, content: string) {
    this.timestamp = timestamp;
    this.level = level;
    this.content = content;
  }

  public toDom() {
    let color = '0,0,0';
    switch (this.level) {
      case 'DEBUG':
        color = '0,0,100';
        break;
      case 'INFO':
        color = '0,0,0';
        break;
      case 'WARN':
        color = '100,60,0';
        break;
      case 'ERROR':
        color = '100,0,0';
        break;
      default:
        color = '0,0,0';
    }
    return (
      <div style={{ margin: '10px' }}>
        <div style={{ color: `rgba(${color},0.5)` }}>
          {`${new Date(this.timestamp).toLocaleString()} [${this.level}]`}
        </div>
        <div style={{ color: `rgba(${color},0.8)` }}>{this.content}</div>
      </div>
    );
  }
}

function Logs(props: { logs: string; maxHeight: string }) {
  if (props.logs) {
    let key = 0;
    return (
      <div style={{ overflow: 'auto', maxHeight: props.maxHeight }}>
        {(JSON.parse(props.logs) as LogEntity[]).map(t => (
          <div key={key++}>{new LogEntity(t.timestamp, t.level, t.content).toDom()}</div>
        ))}
      </div>
    );
  }
  return <div />;
}

export default Logs;
