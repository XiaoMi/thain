package com.xiaomi.thain.common.utils

import org.apache.http.Consts
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.config.SocketConfig
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author liangyongrui
 */
object HttpUtils {

    private val httpClient: CloseableHttpClient

    init {
        val manager = PoolingHttpClientConnectionManager()
        manager.maxTotal = 300
        manager.defaultMaxPerRoute = 300
        manager.defaultSocketConfig = SocketConfig.custom().setSoTimeout(5 * 60 * 1000).build()
        httpClient = HttpClients.custom().setConnectionManager(manager).build()
    }

    @JvmStatic
    @Throws(IOException::class)
    fun postForm(url: String, data: Map<String, *>): String {
        return post(url, emptyMap(), data)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun post(url: String, headers: Map<String, String>, data: Map<String, *>): String {
        val httpPost = HttpPost(url)
        headers.forEach { httpPost.addHeader(it.key, it.value) }
        val formParams = data.map { BasicNameValuePair(it.key, it.value.toString()) }
        val urlEntity = UrlEncodedFormEntity(formParams, Consts.UTF_8)
        httpPost.entity = urlEntity
        httpPost.config = RequestConfig.custom().build()
        return httpClient.execute(httpPost).use { response ->
            response.entity?.let { EntityUtils.toString(it, StandardCharsets.UTF_8) } ?: ""
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    operator fun get(url: String): String {
        val httpGet = HttpGet(url)
        return httpClient.execute(httpGet).use { response ->
            response.entity?.let { EntityUtils.toString(it, StandardCharsets.UTF_8) } ?: ""
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    operator fun get(url: String, data: Map<String, String>): String {
        val condition = data.entries.joinToString("&") { it.key + "=" + it.value }
        val finalUrl = when {
            condition.isEmpty() -> url
            url.contains("?") -> "$url&$condition"
            else -> "$url?$condition"
        }
        return get(finalUrl)
    }

}
