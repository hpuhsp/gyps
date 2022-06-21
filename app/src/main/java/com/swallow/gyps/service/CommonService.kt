package com.swallow.gyps.service

import com.swallow.fly.http.result.BaseResponse
import com.swallow.gyps.test.VersionInfoEntity
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/12/27 10:06
 * @UpdateRemark:   更新说明：
 */
interface CommonService {
    
    /**
     * 获取App最新版本信息
     */
    @GET("company/appVersion/getMaxVersionInfo")
    suspend fun getVersionInfo(@Query("versionType") versionType: String): BaseResponse<VersionInfoEntity>
    
}