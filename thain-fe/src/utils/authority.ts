/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
// use localStorage to store the authority info, which might be sent from server in actual project.
export function getAuthority(str?: string): any {
  const authorityString = typeof str === 'undefined' ? localStorage.getItem('authority') : str;
  let authority;
  try {
    authority = JSON.parse(authorityString!);
  } catch (e) {
    authority = authorityString;
  }
  if (typeof authority === 'string') {
    return [authority];
  }
  return authority;
}

export function setAuthority(authority: string | string[]): void {
  const proAuthority = typeof authority === 'string' ? [authority] : authority;
  return localStorage.setItem('authority', JSON.stringify(proAuthority));
}
