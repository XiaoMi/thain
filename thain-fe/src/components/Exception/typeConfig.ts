import { formatMessage } from 'umi-plugin-react/locale';

/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
interface Config {
  403: {
    img: string;
    title: string;
    desc: string;
  };
  404: {
    img: string;
    title: string;
    desc: string;
  };
  500: {
    img: string;
    title: string;
    desc: string;
  };
}
const config: Config = {
  403: {
    img: 'https://gw.alipayobjects.com/zos/rmsportal/wZcnGqRDyhPOEYFcZDnb.svg',
    title: '403',
    desc: formatMessage({
      id: 'global.deny.messgae',
    }),
  },
  404: {
    img: 'https://gw.alipayobjects.com/zos/rmsportal/KpnpchXsobRgLElEozzI.svg',
    title: '404',
    desc: formatMessage({
      id: 'global.absent.message',
    }),
  },
  500: {
    img: 'https://gw.alipayobjects.com/zos/rmsportal/RVRUAYdCGeYNBWoKiIwB.svg',
    title: '500',
    desc: formatMessage({
      id: 'global.server.error.message',
    }),
  },
};

export default config;
