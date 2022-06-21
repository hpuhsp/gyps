package com.swallow.gyps.test

import com.swallow.fly.base.repository.BaseRepository
import com.swallow.fly.http.result.BaseResponse
import com.swallow.fly.http.result.HttpResult
import com.swallow.gyps.service.CommonService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/12/21 11:23
 * @UpdateRemark:   更新说明：
 */
class LoginRepository @Inject constructor() : BaseRepository() {
    
    
    
    /**
     *根据code或id获取APP版本信息
     */
    fun getVersionInfo(versionCode: String): Flow<HttpResult<BaseResponse<VersionInfoEntity>>> {
        return flow {
            val versionResponse =
                obtainService(CommonService::class.java).getVersionInfo(versionCode)
            emit(HttpResult.Success(versionResponse))
        }.retry(1)
            .flowOn(Dispatchers.IO)
            .catch {
                handleError(it)
            }
    }
}