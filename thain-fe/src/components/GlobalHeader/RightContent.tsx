/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { ConnectProps, ConnectState } from '@/models/connect';
import React, { Component } from 'react';
import { Icon, Tooltip } from 'antd';
import { formatMessage } from 'umi-plugin-react/locale';
import SelectLang from '../SelectLang';
import styles from './index.less';
import Avatar from './AvatarDropdown';
import { connect } from 'dva';

export type SiderTheme = 'light' | 'dark';
export interface GlobalHeaderRightProps extends ConnectProps {
  theme?: SiderTheme;
  layout: 'sidemenu' | 'topmenu';
}

class GlobalHeaderRight extends Component<GlobalHeaderRightProps> {
  render() {
    const { theme, layout } = this.props;
    let className = styles.right;

    if (theme === 'dark' && layout === 'topmenu') {
      className = `${styles.right}  ${styles.dark}`;
    }

    return (
      <div className={className}>
        <Tooltip
          title={formatMessage({
            id: 'global.help',
          })}
        >
          <a
            target="_blank"
            href="https://xiaomi.github.io/thain/"
            rel="noopener noreferrer"
            className={styles.action}
          >
            <Icon type="question-circle-o" />
          </a>
        </Tooltip>
        <Avatar />
        <SelectLang className={styles.action} />
      </div>
    );
  }
}

export default connect(({ settings }: ConnectState) => ({
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
