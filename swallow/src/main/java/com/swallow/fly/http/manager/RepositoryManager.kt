package com.swallow.fly.http.manager

import android.app.Application
import android.content.Context
import com.swallow.fly.http.ResponseErrorListener
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:  对网络请求进行进一步封装，简化外部调用逻辑
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/12 11:18
 * @UpdateRemark:   
 *   - 2024/12: 添加缓存支持
 */
@Singleton
class RepositoryManager @Inject constructor(
    private val application: Application,
    private val retrofit: Retrofit,
    private var errorListener: ResponseErrorListener
) : IRepositoryManager {

    // 简单的内存缓存（生产环境建议使用 Room 或 MMKV）
    private val memoryCache = mutableMapOf<String, Any>()

    override fun <T> obtainRetrofitService(service: Class<T>): T {
        return retrofit.create(service)
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
}