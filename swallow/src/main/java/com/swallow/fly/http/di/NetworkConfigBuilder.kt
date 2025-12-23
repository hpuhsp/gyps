package com.swallow.fly.http.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.swallow.fly.http.ResponseErrorListener
import com.swallow.fly.http.interceptor.GlobalHttpHandler
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @Description: 网络配置构建器
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的配置构建器
 * 
 * 使用示例：
 * ```kotlin
 * override fun configureNetwork(context: Context, builder: NetworkConfigBuilder) {
 *     builder
 *         .baseUrl("https://api.example.com/")
 *         .globalHttpHandler(MyHttpHandler())
 *         .gsonConfiguration(object : GsonConfiguration {
 *             override fun configGson(context: Context?, builder: GsonBuilder?) {
 *                 builder?.serializeNulls()
 *             }
 *         })
 * }
 * ```
 */
class NetworkConfigBuilder {
    
    var baseUrl: HttpUrl? = null
        private set
    
    var handler: GlobalHttpHandler? = null
        private set
    
    var responseErrorListener: ResponseErrorListener? = null
        private set
    
    var retrofitConfiguration: RetrofitConfiguration? = null
        private set
    
    var okhttpConfiguration: OkhttpConfiguration? = null
        private set
    
    var gsonConfiguration: GsonConfiguration? = null
        private set
    
    /**
     * 配置基础 URL
     * 
     * @param url 基础 URL 字符串
     * @return 当前构建器，支持链式调用
     */
    fun baseUrl(url: String): NetworkConfigBuilder {
        this.baseUrl = url.toHttpUrlOrNull()
        return this
    }
    
    /**
     * 配置全局 HTTP 处理器
     * 
     * @param handler 全局 HTTP 处理器
     * @return 当前构建器，支持链式调用
     */
    fun globalHttpHandler(handler: GlobalHttpHandler): NetworkConfigBuilder {
        this.handler = handler
        return this
    }
    
    /**
     * 配置响应错误监听器
     * 
     * @param listener 响应错误监听器
     * @return 当前构建器，支持链式调用
     */
    fun responseErrorListener(listener: ResponseErrorListener): NetworkConfigBuilder {
        this.responseErrorListener = listener
        return this
    }
    
    /**
     * 配置 Retrofit
     * 
     * @param config Retrofit 配置接口
     * @return 当前构建器，支持链式调用
     */
    fun retrofitConfiguration(config: RetrofitConfiguration): NetworkConfigBuilder {
        this.retrofitConfiguration = config
        return this
    }
    
    /**
     * 配置 OkHttp
     * 
     * @param config OkHttp 配置接口
     * @return 当前构建器，支持链式调用
     */
    fun okhttpConfiguration(config: OkhttpConfiguration): NetworkConfigBuilder {
        this.okhttpConfiguration = config
        return this
    }
    
    /**
     * 配置 Gson
     * 
     * @param config Gson 配置接口
     * @return 当前构建器，支持链式调用
     */
    fun gsonConfiguration(config: GsonConfiguration): NetworkConfigBuilder {
        this.gsonConfiguration = config
        return this
    }
}

/**
 * Retrofit 配置接口
 */
interface RetrofitConfiguration {
    fun configRetrofit(context: Context, builder: Retrofit.Builder)
}

/**
 * OkHttp 配置接口
 */
interface OkhttpConfiguration {
    fun configOkhttp(context: Context, builder: OkHttpClient.Builder)
}

/**
 * Gson 配置接口
 */
interface GsonConfiguration {
    fun configGson(context: Context?, builder: GsonBuilder?)
}
