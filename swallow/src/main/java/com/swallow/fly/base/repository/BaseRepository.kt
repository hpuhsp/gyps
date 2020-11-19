package com.swallow.fly.base.repository

import com.swallow.fly.http.manager.RepositoryManager
import javax.inject.Inject

/**
 * @Description:
 * 调用实例如下(注意Kotlin Flow操作符顺序)
 *  return flow {
 *  val response = obtainService(CommonService::class.java).fetchOfflineBasicData(userCode)
 *  emit(HttpResult.Success(response))
 *  }
 *  .retry(1)
 *  .flowOn(Dispatchers.IO)
 *  .catch {
 *  handleError(it)
 *  }
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 12:00
 * @UpdateRemark:   更新说明：
 */
abstract class BaseRepository {
    /**
     * 全局数据请求处理
     */
    @Inject
    lateinit var repositoryManager: RepositoryManager
    // 可拓展配置网络缓存逻辑
//    @Inject
//    RxCache

    /**
     *=====================================网络基本配置================================================
     */
    fun <T> obtainService(service: Class<T>): T {
        return repositoryManager.obtainRetrofitService(service)
    }

    fun handleError(cause: Throwable?): Throwable? {
        return repositoryManager.handleResponseError(cause)
    }
}