package com.hnsh.core.http.manager

import android.app.Application
import android.content.Context
import com.hnsh.core.ext.logd
import com.hnsh.core.ext.logi
import com.hnsh.core.http.ResponseErrorListener
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:  对网络请求进行进一步封装，简化外部调用逻辑
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/12 11:18
 * @UpdateRemark:   更新说明：
 */
@Singleton
class RepositoryManager @Inject constructor(
    private val application: Application,
    private val retrofit: Retrofit,
    private var errorListener: ResponseErrorListener
) : IRepositoryManager {

    override fun <T> obtainRetrofitService(service: Class<T>): T {
        return retrofit.create(service)
    }

    override fun <T> obtainCacheService(cache: Class<T>): T {
        TODO("Not yet implemented")
    }

    override fun clearAllCache() {
        // 暂时未做处理
    }

    /**
     * 处理错误
     */
    fun handleResponseError(cause: Throwable?): Throwable? {
        return errorListener.handleResponseError(cause)
    }

    /**
     * 必要时使用的App上下文对象
     */
    override fun getContext(): Context? {
        return application
    }

}