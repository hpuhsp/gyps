package com.swallow.fly.base.data

import com.swallow.fly.annotations.Stable
import com.swallow.fly.http.result.HttpResult
import com.swallow.fly.http.manager.RepositoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Repository 基类，提供数据访问层的通用功能
 *
 * 核心特性：
 * - **统一错误处理**：通过 [executeRequest] 自动捕获异常并转换为 [HttpResult]
 * - **Flow 支持**：提供 [flowRequest] 和 [cachedFlowRequest] 用于响应式数据流
 * - **缓存策略**：支持 Stale-While-Revalidate (SWR) 模式，提供极致的"秒开"体验
 * - **服务获取**：通过 [obtainService] 获取网络服务接口
 *
 * 关于缓存策略的说明：
 * 虽然 Retrofit/OkHttp 支持 HTTP 协议级缓存 (Cache-Control)，但它主要用于节省带宽。
 * 而 BaseRepository 中的 [cachedFlowRequest] 实现了 "Stale-While-Revalidate" (SWR) 策略：
 * 1. 立即发射本地缓存（如果是 App 级持久化数据），让 UI 瞬间显示
 * 2. 同时发起网络请求，获取最新数据
 * 3. 更新缓存并发射最新数据
 *
 * 这种"双发射"模式能提供比单纯 HTTP 缓存更极致的"秒开"体验。
 *
 * 使用示例：
 * ```kotlin
 * @Singleton
 * class UserRepository @Inject constructor() : BaseRepository() {
 *
 *     // 简单的网络请求
 *     fun getUserInfo(userId: String): Flow<HttpResult<User>> {
 *         return flowRequest {
 *             obtainService(UserService::class.java).getUserInfo(userId)
 *         }
 *     }
 *
 *     // 带缓存的网络请求（SWR 模式）
 *     fun getUserInfoCached(userId: String): Flow<HttpResult<User>> {
 *         return cachedFlowRequest("user_$userId") {
 *             obtainService(UserService::class.java).getUserInfo(userId)
 *         }
 *     }
 * }
 * ```
 *
 * 在 ViewModel 中使用：
 * ```kotlin
 * class UserViewModel @Inject constructor(
 *     private val repository: UserRepository
 * ) : BaseViewModel() {
 *
 *     fun loadUser(userId: String) {
 *         launchOnUI {
 *             repository.getUserInfo(userId).collect { result ->
 *                 result.onSuccess { user ->
 *                     _uiState.value = UiState.Success(user)
 *                 }.onFailure { error ->
 *                     _uiState.value = UiState.Error(error?.message ?: "Unknown error")
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @see IRepository
 * @see HttpResult
 * @see BaseRepositoryBoth
 * @see BaseRepositoryLocal
 * @see BaseRepositoryRemote
 * @see BaseRepositoryNothing
 *
 * @since 1.0.0
 * @author Hsp
 */
@Stable
abstract class BaseRepository : IRepository {
    /**
     * 全局数据请求处理
     */
    @Inject
    lateinit var repositoryManager: RepositoryManager

    /**
     * 获取网络服务接口
     */
    protected fun <T> obtainService(service: Class<T>): T {
        return repositoryManager.obtainService(service)
    }

    /**
     * 统一请求执行（带错误处理）
     * @param request 请求操作
     * @return HttpResult<T> 包装的结果
     */
    override suspend fun <T> executeRequest(request: suspend () -> T): HttpResult<T> {
        return try {
            HttpResult.Success(request())
        } catch (e: Exception) {
            val error = repositoryManager.handleResponseError(e)
            HttpResult.Failure(error ?: e)
        }
    }

    /**
     * Flow 包装器 - 自动在 IO 线程执行
     * @param request 请求操作
     * @return Flow<HttpResult<T>>
     */
    protected fun <T> flowRequest(request: suspend () -> T): Flow<HttpResult<T>> = flow {
        emit(executeRequest(request))
    }.flowOn(Dispatchers.IO)

    /**
     * 带缓存的 Flow 请求 (Stale-While-Revalidate 模式)
     * 优先展示缓存，同时请求网络更新
     *
     * @param cacheKey 缓存键
     * @param request 请求操作
     * @return Flow<HttpResult<T>>
     */
    protected fun <T> cachedFlowRequest(
        cacheKey: String,
        request: suspend () -> T
    ): Flow<HttpResult<T>> = flow {
        // 1. 尝试获取并发射缓存 (不阻塞，不仅为了省流，更为了秒开)
        try {
            val cached = repositoryManager.getCache<T>(cacheKey)
            if (cached != null) {
                emit(HttpResult.Success(cached))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 缓存读取失败不应阻断网络请求
        }

        // 2. 发起网络请求
        val result = executeRequest(request)

        // 3. 网络请求成功则更新缓存
        if (result is HttpResult.Success) {
            repositoryManager.saveCache(cacheKey, result.value)
        }

        // 4. 发射网络结果 (覆盖缓存)
        emit(result)
    }.flowOn(Dispatchers.IO)

    /**
     * 处理错误
     * @Deprecated 使用 executeRequest 自动处理错误
     */
    @Deprecated("Use executeRequest instead", ReplaceWith("executeRequest { }"))
    fun handleError(cause: Throwable?): Throwable? {
        return repositoryManager.handleResponseError(cause)
    }
}
