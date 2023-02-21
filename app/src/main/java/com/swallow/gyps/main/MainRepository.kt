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
     * 获取设备 待维修/正在维修列表
     */
    fun reportHealthyStatus(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
        return flow {
            val response = obtainService(HealthyService::class.java).reportHealthyStatus(model)
            emit(HttpResult.Success(response))
        }.retry(1)
            .flowOn(Dispatchers.IO)
            .catch {
                handleError(it)
            }
    }

    /**
     * 获取设备 待维修/正在维修列表
     */
    fun reportHealthyStatus2(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
        return flow {
            val response = obtainService(HealthyService::class.java).reportHealthyStatus2(model)
            emit(HttpResult.Success(response))
        }.retry(1)
            .flowOn(Dispatchers.IO)
            .catch {
                handleError(it)
            }
    }
}