/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
export default {
  '200': 'The server successfully returned the requested data.',
  '201': 'Successful data creation or modification.',
  '202': 'A request has entered the background queue (asynchronous task).',
  '204': 'Data deletion was successful.',
  '400': 'There was an error in the request, and the server did not create or modify the data.',
  '401': 'Users do not have permission (token, username or password error).',
  '403': 'Users are authorized, but access is prohibited.',
  '404': 'The request is for non-existent records, and the server is not operating.',
  '406': 'The format of the request is not available.',
  '410': 'The requested resource is permanently deleted and will not be retrieved.',
  '422': 'When an object is created, a validation error occurs.',
  '500': 'Server error, please check the server.',
  '502': 'Gateway error.',
  '503': 'The service is unavailable and the server is temporarily overloaded or maintained.',
  '504': 'Gateway timeout.',
};
