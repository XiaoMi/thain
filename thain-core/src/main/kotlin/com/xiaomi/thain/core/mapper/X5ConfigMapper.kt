package com.xiaomi.thain.core.mapper

import com.xiaomi.thain.core.model.dr.X5ConfigDr

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
interface X5ConfigMapper {

    fun getX5ConfigByAppId(appId:String): X5ConfigDr?

}
