package com.swallow.gyps.main

import com.swallow.fly.base.repository.BaseRepository
import com.swallow.fly.http.result.BaseResponse
import com.swallow.fly.http.result.HttpResult
import com.swallow.gyps.main.models.HealthModel
import com.swallow.gyps.service.HealthyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/24 10:29
 * @UpdateRemark:   更新说明：
 */
class MainRepository @Inject constructor() : BaseRepository() {

    /**
     * 健康状态上报
     * 使用新的 BaseRepository API
     */
    fun reportHealthyStatus(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
        return flow {
            val result = executeRequest {
                obtainService(HealthyService::class.java).reportHealthyStatus(model)
            }
            
            result.fold(
                onSuccess = { response ->
                    emit(HttpResult.Success(response))
                },
                onFailure = { error ->
                    emit(HttpResult.Failure(error))
                }
            )
        }.flowOn(Dispatchers.IO)
    }
}