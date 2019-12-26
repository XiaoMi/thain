package com.xiaomi.thain.component.tools

import java.io.IOException

/**
 * Date 19-5-30 下午3:41
 *
 * @author liangyongrui@xiaomi.com
 */
interface ComponentTools {
    /**
     * 发送邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 正文
     */
    fun sendMail(to: Array<String>, subject: String, content: String)

    /**
     * 保存当前节点产生的数据
     *
     * @param key   数据的key
     * @param value 数据的value
     */
    fun putStorage(key: String, value: Any)

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName 节点名称
     * @param key     key
     * @param <T>     自动强制转换
     * @return 返回对应值的Optional
    </T> */
    fun <T> getStorageValue(jobName: String, key: String): T?

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName      节点名称
     * @param key          key
     * @param defaultValue 默认值
     * @param <T>          自动强制转换
     * @return 返回对应值, 值不存在则返回defaultValue
    </T> */
    fun <T> getStorageValueOrDefault(jobName: String, key: String, defaultValue: T): T

    /**
     * 增加debug日志
     */
    fun addDebugLog(content: String)

    /**
     * 增加info日志
     */
    fun addInfoLog(content: String)

    /**
     * 增加warning日志
     */
    fun addWarnLog(content: String)

    /**
     * 增加error日志
     *
     * @param content
     */
    fun addErrorLog(content: String)

    /**
     * 发送http get 请求
     *
     * @param url  url
     * @param data ?后面的
     */
    @Throws(IOException::class)
    fun httpGet(url: String, data: Map<String, String>): String

    /**
     * 发送 http post 请求
     *
     * @param url     url
     * @param headers headers
     * @param data    data
     */
    @Throws(IOException::class)
    fun httpPost(url: String,
                 headers: Map<String, String>,
                 data: Map<String, *>): String

    /**
     * 获取当前的id
     */
    fun getJobExecutionId(): Long

    /**
     * 获取flow当前产生的全部结果
     */
    fun getStorage(): Map<Pair<String, String>, Any>

    fun httpX5Post(url: String, data: Map<String, String>): String
}
