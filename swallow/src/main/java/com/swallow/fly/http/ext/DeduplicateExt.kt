package com.swallow.fly.http.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import java.util.concurrent.ConcurrentHashMap

/**
 * Flow 请求去重扩展
 *
 * 防止短时间内发起相同的请求,提升性能和用户体验
 *
 * 使用场景:
 * ```kotlin
 * // Repository 中
 * fun getUserInfo(userId: String): Flow<HttpResult<User>> {
 *     return flowRequest {
 *         obtainService(UserService::class.java).getUserInfo(userId)
 *     }
 *     .unwrap()
 *     .deduplicate("user_$userId", timeout = 2000L)  // 2秒内去重
 * }
 * ```
 *
 * 工作原理:
 * 1. 第一次请求: 正常执行,并缓存 Flow
 * 2. 相同 key 的后续请求: 在超时时间内,复用第一次请求的 Flow
 * 3. 超时后: 清除缓存,下次请求重新执行
 *
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 2.0.0
 */

/**
 * 请求去重
 *
 * 相同 key 的请求在指定时间内只执行一次
 *
 * @param key 请求唯一标识 (建议格式: "api_name_param1_param2")
 * @param timeout 去重时间窗口(毫秒,默认 1000ms)
 * @return 去重后的 Flow
 */
fun <T> Flow<T>.deduplicate(
    key: String,
    timeout: Long = 1000L
): Flow<T> = flow {
    val deduplicator = RequestDeduplicator.getInstance()

    // 检查是否有进行中的请求
    val existingFlow = deduplicator.get<T>(key)
    if (existingFlow != null) {
        // 复用现有请求
        existingFlow.collect { emit(it) }
        return@flow
    }

    // 创建共享 Flow (多个订阅者共享同一个上游)
    val sharedFlow = this@deduplicate.shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1  // 缓存最后一个结果
    )

    // 缓存当前请求
    deduplicator.put(key, sharedFlow, timeout)

    // 执行请求
    sharedFlow.collect { emit(it) }
}

/**
 * 请求去重管理器
 *
 * 使用单例模式管理所有去重请求
 */
class RequestDeduplicator private constructor() {

    // 缓存: key -> (Flow, 过期时间)
    private val cache = ConcurrentHashMap<String, Pair<Flow<*>, Long>>()

    companion object {
        @Volatile
        private var instance: RequestDeduplicator? = null

        /**
         * 获取单例实例
         */
        fun getInstance(): RequestDeduplicator {
            return instance ?: synchronized(this) {
                instance ?: RequestDeduplicator().also { instance = it }
            }
        }
    }

    /**
     * 获取缓存的 Flow
     *
     * @param key 请求标识
     * @return 缓存的 Flow,如果不存在或已过期则返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): Flow<T>? {
        val (flow, expireTime) = cache[key] ?: return null

        // 检查是否过期
        if (System.currentTimeMillis() > expireTime) {
            cache.remove(key)
            return null
        }

        return flow as? Flow<T>
    }

    /**
     * 缓存 Flow
     *
     * @param key 请求标识
     * @param flow 要缓存的 Flow
     * @param timeout 超时时间(毫秒)
     */
    fun <T> put(key: String, flow: Flow<T>, timeout: Long) {
        val expireTime = System.currentTimeMillis() + timeout
        cache[key] = flow to expireTime

        // 定期清理过期缓存
        cleanupExpiredCache()
    }

    /**
     * 移除指定 key 的缓存
     */
    fun remove(key: String) {
        cache.remove(key)
    }

    /**
     * 清空所有缓存
     */
    fun clear() {
        cache.clear()
    }

    /**
     * 获取缓存大小
     */
    fun size(): Int {
        return cache.size
    }

    /**
     * 清理过期缓存
     */
    private fun cleanupExpiredCache() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = cache.entries
            .filter { (_, pair) -> currentTime > pair.second }
            .map { it.key }

        expiredKeys.forEach { cache.remove(it) }
    }
}

/**
 * 清除指定 key 的去重缓存
 *
 * 使用场景: 强制刷新数据
 *
 * @param key 请求标识
 */
fun clearDeduplicateCache(key: String) {
    RequestDeduplicator.getInstance().remove(key)
}

/**
 * 清除所有去重缓存
 */
fun clearAllDeduplicateCache() {
    RequestDeduplicator.getInstance().clear()
}

/**
 * 获取去重缓存大小
 */
fun getDeduplicateCacheSize(): Int {
    return RequestDeduplicator.getInstance().size()
}
