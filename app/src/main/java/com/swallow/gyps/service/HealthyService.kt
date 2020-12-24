package com.swallow.gyps.service

import com.swallow.fly.http.result.BaseResponse
import com.swallow.gyps.main.models.HealthModel
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/24 10:32
 * @UpdateRemark:   更新说明：
 */
interface HealthyService {
    @POST("add")
    suspend fun reportHealthyStatus(@Body model: HealthModel): BaseResponse<Any>
}