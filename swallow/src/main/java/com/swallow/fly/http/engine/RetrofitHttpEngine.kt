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
     * 注意：Retrofit 默认不支持按标签取消，需要配合 OkHttp 的 Dispatcher
     * 这里提供接口，具体实现可在需要时扩展
     */
    override fun cancelRequest(tag: String) {
        // TODO: 实现基于 OkHttp Dispatcher 的标签取消
        // retrofit.callFactory().dispatcher().queuedCalls()
        //     .filter { it.request().tag() == tag }
        //     .forEach { it.cancel() }
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
}
