package com.swallow.fly.http.manager

import android.app.Application
import android.content.Context
import com.swallow.fly.http.ResponseErrorListener
import com.swallow.fly.http.engine.HttpEngine
import com.tencent.mmkv.MMKV
import java.util.concurrent.ConcurrentHashMap
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
 *   - 2026/03: 内存缓存对接 MMKV 持久化（两级缓存：L1 内存 + L2 MMKV）
 */
@Singleton
class RepositoryManager @Inject constructor(
    private val application: Application,
    private val httpEngine: HttpEngine,
    private var errorListener: ResponseErrorListener
) : IRepositoryManager {

    // L1：内存缓存，线程安全（ConcurrentHashMap 避免并发写入竞态）
    private val memoryCache = ConcurrentHashMap<String, Any>()

    // L2：MMKV 持久化缓存，使用独立 namespace 避免与业务 key 冲突
    private val mmkv: MMKV by lazy {
        MMKV.mmkvWithID(MMKV_NAMESPACE)
    }

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

    /**
     * 获取缓存服务（当前返回 RepositoryManager 自身，供链式调用缓存 API）
     *
     * 注意：此方法仅当 T 为 IRepositoryManager 时有意义，
     * 复杂缓存服务建议直接注入 RepositoryManager 使用 getCache/saveCache。
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> obtainCacheService(cache: Class<T>): T {
        return this as T
    }

    override fun clearAllCache() {
        memoryCache.clear()
        mmkv.clearAll()
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
     * 获取缓存数据（两级缓存：L1 内存优先，miss 时读 L2 MMKV）
     *
     * 注意：MMKV 仅持久化 String 类型，非 String 类型只存在于 L1 内存缓存。
     *
     * @param key 缓存 key
     * @return 缓存值，不存在时返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getCache(key: String): T? {
        // L1 命中直接返回
        val memResult = memoryCache[key] as? T
        if (memResult != null) return memResult
        // L2 读取（仅 String 类型有持久化值），回填 L1
        val mmkvResult = mmkv.decodeString(key) ?: return null
        memoryCache[key] = mmkvResult
        return mmkvResult as? T
    }

    /**
     * 保存缓存数据（同时写入 L1 内存和 L2 MMKV）
     *
     * 仅支持 String 类型持久化到 MMKV；其他类型仅写入内存缓存。
     * 如需持久化复杂对象，请先序列化为 JSON 字符串再调用此方法。
     *
     * @param key 缓存 key
     * @param data 缓存值
     */
    fun <T> saveCache(key: String, data: T?) {
        if (data == null) {
            removeCache(key)
            return
        }
        memoryCache[key] = data as Any
        // String 类型直接持久化；其他类型仅内存缓存
        if (data is String) {
            mmkv.encode(key, data)
        }
    }

    /**
     * 移除缓存数据（同时清除 L1 和 L2）
     *
     * @param key 缓存 key
     */
    fun removeCache(key: String) {
        memoryCache.remove(key)
        mmkv.removeValueForKey(key)
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

    companion object {
        private const val MMKV_NAMESPACE = "swallow_repository_cache"
    }
}
