package com.swallow.fly.http.engine

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit 网络引擎实现
 * 
 * @Description: 基于 Retrofit 的 HttpEngine 实现（默认实现）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2026/01/02
 * @UpdateRemark: 
 *   - 封装 Retrofit 作为默认网络库
 *   - 支持 Service 接口创建
 */
@Singleton
class RetrofitHttpEngine @Inject constructor(
    private val retrofit: Retrofit
) : HttpEngine {
    
    /**
     * 使用 Retrofit 创建服务接口实例
     */
    override fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
    
    /**
     * 取消指定标签的请求
     * 
     * 通过 OkHttp Dispatcher 取消带有指定标签的请求
     * 
     * @param tag 请求标签
     */
    override fun cancelRequest(tag: String) {
        retrofit.callFactory()?.let { factory ->
            if (factory is okhttp3.OkHttpClient) {
                val dispatcher = factory.dispatcher
                
                // 取消队列中的请求
                dispatcher.queuedCalls()
                    .filter { it.request().tag() == tag }
                    .forEach { it.cancel() }
                
                // 取消正在执行的请求
                dispatcher.runningCalls()
                    .filter { it.request().tag() == tag }
                    .forEach { it.cancel() }
            }
        }
    }
    
    /**
     * 取消所有请求
     */
    override fun cancelAllRequests() {
        retrofit.callFactory()?.let { factory ->
            if (factory is okhttp3.OkHttpClient) {
                factory.dispatcher.cancelAll()
            }
        }
    }
    
    /**
     * 获取当前活跃的请求数量
     * 
     * @return 正在执行的请求数量
     */
    fun getActiveRequestCount(): Int {
        retrofit.callFactory()?.let { factory ->
            if (factory is okhttp3.OkHttpClient) {
                return factory.dispatcher.runningCallsCount()
            }
        }
        return 0
    }
    
    /**
     * 获取队列中等待的请求数量
     * 
     * @return 队列中的请求数量
     */
    fun getQueuedRequestCount(): Int {
        retrofit.callFactory()?.let { factory ->
            if (factory is okhttp3.OkHttpClient) {
                return factory.dispatcher.queuedCallsCount()
            }
        }
        return 0
    }
}
