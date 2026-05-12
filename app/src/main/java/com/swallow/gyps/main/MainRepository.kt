package com.swallow.gyps.main

import com.swallow.fly.base.data.BaseRepository
import com.swallow.fly.http.ext.retryWithExponentialBackoff
import com.swallow.fly.http.ext.unwrap
import com.swallow.fly.http.result.BaseResponse
import com.swallow.fly.http.result.HttpResult
import com.swallow.gyps.main.models.HealthModel
import com.swallow.gyps.service.HealthyService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @Description: 主页相关数据仓库
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/24 10:29
 * @UpdateRemark:   更新说明：
 *  - 2024/12: 使用 flowRequest 简化代码
 *  - 2026/03/13: 使用新的网络层扩展 API
 */
class MainRepository @Inject constructor() : BaseRepository() {

    /**
     * 健康状态上报 (旧接口 - 保持兼容)
     */
    fun reportHealthyStatus(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
        return flowRequest {
            obtainService(HealthyService::class.java).reportHealthyStatus(model)
        }
    }

    /**
     * 健康状态上报 (优化版本)
     *
     * 新特性:
     * - 自动解包 BaseResponse
     * - 智能重试(最多 2 次,健康上报不需要太多重试)
     *
     * 使用示例:
     * ```kotlin
     * // ViewModel 中
     * repository.reportHealthyStatusOptimized(model).collect { result ->
     *     result.onSuccess { data ->  // 直接得到结果
     *         // 上报成功
     *     }
     * }
     * ```
     */
    fun reportHealthyStatusOptimized(model: HealthModel): Flow<HttpResult<Any>> {
        return flowRequest {
            obtainService(HealthyService::class.java).reportHealthyStatus(model)
        }
        .unwrap()  // 自动解包 BaseResponse
        .retryWithExponentialBackoff(maxRetries = 2)  // 智能重试
    }
}