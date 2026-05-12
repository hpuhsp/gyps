package com.swallow.gyps.test

import com.swallow.fly.base.data.BaseRepository
import com.swallow.fly.http.ext.deduplicate
import com.swallow.fly.http.ext.retryWithExponentialBackoff
import com.swallow.fly.http.ext.unwrap
import com.swallow.fly.http.result.BaseResponse
import com.swallow.fly.http.result.HttpResult
import com.swallow.gyps.service.CommonService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @Description: 登录相关数据仓库
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/12/21 11:23
 * @UpdateRemark:
 *   - 2026/03/13: 使用新的网络层扩展 API
 */
class LoginRepository @Inject constructor() : BaseRepository() {

    /**
     * 根据 code 或 id 获取 APP 版本信息 (旧接口 - 保持兼容)
     */
    fun getVersionInfo(versionCode: String): Flow<HttpResult<BaseResponse<VersionInfoEntity>>> {
        return flowRequest {
            obtainService(CommonService::class.java).getVersionInfo(versionCode)
        }
    }

    /**
     * 根据 code 或 id 获取 APP 版本信息 (优化版本)
     *
     * 新特性:
     * - 自动解包 BaseResponse
     * - 智能重试(最多 3 次)
     * - 请求去重(2 秒内)
     *
     * 使用示例:
     * ```kotlin
     * // ViewModel 中
     * repository.getVersionInfoOptimized("1.0.0").collect { result ->
     *     result.onSuccess { version ->  // 直接得到 VersionInfoEntity
     *         _uiState.value = UiState.Success(version)
     *     }
     * }
     * ```
     */
    fun getVersionInfoOptimized(versionCode: String): Flow<HttpResult<VersionInfoEntity>> {
        return flowRequest {
            obtainService(CommonService::class.java).getVersionInfo(versionCode)
        }
        .unwrap()  // 自动解包 BaseResponse
        .retryWithExponentialBackoff(maxRetries = 3)  // 智能重试
        .deduplicate("version_$versionCode", timeout = 2000L)  // 请求去重
    }
}