package com.swallow.fly.base.data

import com.swallow.fly.http.manager.RepositoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @Description: 现代化 Repository 基类
 * 调用实例如下(注意Kotlin Flow操作符顺序)
 *  return flowRequest {
 *      obtainService(CommonService::class.java).fetchOfflineBasicData(userCode)
 *  }
 * 
 * 或使用带缓存的请求：
 *  return cachedFlowRequest("cache_key") {
 *      obtainService(CommonService::class.java).fetchData()
 *  }
 * 
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 12:00
 * @UpdateRemark:   
 *   - 2024/12: 升级到现代化架构
 *   - 实现 IRepository 接口
 *   - 添加 Flow 支持
 *   - 统一错误处理
 *   - 支持缓存策略
 */
abstract class BaseRepository : IRepository {
    /**
     * 全局数据请求处理
     */
    @Inject
    lateinit var repositoryManager: RepositoryManager

    /**
     * 获取 Retrofit Service
     */
    protected fun <T> obtainService(service: Class<T>): T {
        return repositoryManager.obtainRetrofitService(service)
    }

    /**
     * 统一请求执行（带错误处理）
     * @param request 请求操作
     * @return Result<T> 包装的结果
     */
    override suspend fun <T> executeRequest(request: suspend () -> T): Result<T> {
        return try {
            Result.success(request())
        } catch (e: Exception) {
            val error = repositoryManager.handleResponseError(e)
            Result.failure(error ?: e)
        }
    }

    /**
     * Flow 包装器 - 自动在 IO 线程执行
     * @param request 请求操作
     * @return Flow<Result<T>>
     */
    protected fun <T> flowRequest(request: suspend () -> T): Flow<Result<T>> = flow {
        emit(executeRequest(request))
    }.flowOn(Dispatchers.IO)

    /**
     * 带缓存的 Flow 请求
     * @param cacheKey 缓存键
     * @param request 请求操作
     * @return Flow<Result<T>>
     */
    protected fun <T> cachedFlowRequest(
        cacheKey: String,
        request: suspend () -> T
    ): Flow<Result<T>> = flow {
        // 先发射缓存数据（如果有）
        val cached = repositoryManager.getCache<T>(cacheKey)
        if (cached != null) {
            emit(Result.success(cached))
        }
        
        // 再请求网络数据
        val result = executeRequest(request)
        if (result.isSuccess) {
            repositoryManager.saveCache(cacheKey, result.getOrNull())
        }
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
