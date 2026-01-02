package com.swallow.fly.http.manager

import android.app.Application
import android.content.Context
import com.swallow.fly.http.ResponseErrorListener
import com.swallow.fly.http.engine.HttpEngine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:  对网络请求进行进一步封装，简化外部调用逻辑
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/12 11:18
 * @UpdateRemark:
 *   - 2024/12: 添加缓存支持
 *   - 2026/01: 重构为依赖 HttpEngine 抽象，支持多种网络库
 */
@Singleton
class RepositoryManager @Inject constructor(
    private val application: Application,
    private val httpEngine: HttpEngine,  // 依赖抽象而非具体实现
    private var errorListener: ResponseErrorListener
) : IRepositoryManager {

    // 简单的内存缓存（生产环境建议使用 Room 或 MMKV）
    private val memoryCache = mutableMapOf<String, Any>()

    /**
     * 获取网络服务接口实例
     * 
     * 委托给 HttpEngine，而不是直接使用 Retrofit
     * 这样未来可以切换到 Ktor 或其他网络库而无需修改此处代码
     */
    override fun <T> obtainService(service: Class<T>): T {
        return httpEngine.createService(service)
    }
    
    /**
     * @deprecated 请使用 obtainService 替代
     */
    @Deprecated("Use obtainService instead")
    override fun <T> obtainRetrofitService(service: Class<T>): T {
        return obtainService(service)
    }

    override fun <T> obtainCacheService(cache: Class<T>): T {
        TODO("Not yet implemented")
    }

    override fun clearAllCache() {
        memoryCache.clear()
    }

    /**
     * 处理错误
     */
    fun handleResponseError(cause: Throwable?): Throwable? {
        return errorListener.handleResponseError(cause)
    }

    /**
     * 必要时使用的App上下文对象
     */
    override fun getContext(): Context? {
        return application
    }

    /**
     * 获取缓存数据
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getCache(key: String): T? {
        return memoryCache[key] as? T
    }

    /**
     * 保存缓存数据
     */
    fun <T> saveCache(key: String, data: T?) {
        if (data != null) {
            memoryCache[key] = data as Any
        }
    }

    /**
     * 移除缓存数据
     */
    fun removeCache(key: String) {
        memoryCache.remove(key)
    }
    
    /**
     * 取消指定标签的请求
     */
    fun cancelRequest(tag: String) {
        httpEngine.cancelRequest(tag)
    }
    
    /**
     * 取消所有请求
     */
    fun cancelAllRequests() {
        httpEngine.cancelAllRequests()
    }
}