package com.swallow.fly.http.manager

import android.content.Context
import androidx.annotation.NonNull
import com.swallow.fly.annotations.Stable

/**
 * Repository 管理器接口
 *
 * 提供全局的网络服务获取、缓存管理和错误处理功能
 *
 * 核心功能：
 * - **服务获取**：通过 [obtainService] 获取网络服务接口
 * - **缓存管理**：提供缓存服务的获取和清理
 * - **上下文访问**：提供全局 Context 访问
 *
 * 使用示例：
 * ```kotlin
 * @Singleton
 * class MyRepository @Inject constructor() : BaseRepository() {
 *
 *     fun getUserInfo(userId: String): Flow<HttpResult<User>> {
 *         return flowRequest {
 *             // 通过 repositoryManager 获取服务
 *             repositoryManager.obtainService(UserService::class.java).getUserInfo(userId)
 *         }
 *     }
 * }
 * ```
 *
 * @see BaseRepository
 *
 * @since 1.0.0
 * @author Hsp
 */
@Stable
interface IRepositoryManager {
    
    /**
     * 获取网络服务接口（推荐使用）
     * 
     * 通过 Retrofit 动态代理创建服务接口实例
     * 
     * @param T 服务接口类型
     * @param service 服务接口的 Class 对象
     * @return 服务接口的实现实例
     */
    @NonNull
    fun <T> obtainService(@NonNull service: Class<T>): T
    
    /**
     * 获取 Retrofit 服务接口（已废弃）
     * 
     * @deprecated 请使用 [obtainService] 替代，新方法不绑定特定网络库
     */
    @Deprecated(
        message = "Use obtainService instead. This method name is coupled to Retrofit.",
        replaceWith = ReplaceWith("obtainService(service)"),
        level = DeprecationLevel.WARNING
    )
    @NonNull
    fun <T> obtainRetrofitService(@NonNull service: Class<T>): T

    /**
     * 获取缓存服务
     * 
     * @param T 缓存服务类型
     * @param cache 缓存服务的 Class 对象
     * @return 缓存服务实例
     */
    @NonNull
    fun <T> obtainCacheService(@NonNull cache: Class<T>): T

    /**
     * 清除所有缓存
     */
    fun clearAllCache()

    /**
     * 获取全局 Context
     * 
     * @return Application Context
     */
    @NonNull
    fun getContext(): Context?
}