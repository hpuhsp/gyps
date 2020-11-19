package com.swallow.fly.http.interceptor

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception

/**
 * @Description: 处理 Http 请求和响应结果的处理类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/29 14:38
 * @UpdateRemark:   更新说明：
 */
interface GlobalHttpHandler {

    /**
     * 这里可以先客户端一步拿到每一次 Http 请求的结果, 可以先解析成 Json, 再做一些操作, 如检测到 token 过期后
     * 重新请求 token, 并重新执行请求
     *
     * @param httpResult 服务器返回的结果 (已被框架自动转换为字符串)
     * @param chain      [okhttp3.Interceptor.Chain]
     * @param response   [Response]
     * @return [Response]
     */
    @NonNull
    fun onHttpResultResponse(
        @Nullable httpResult: String?,
        @NonNull chain: Interceptor.Chain,
        @NonNull response: Response
    ): Response

    /**
     * 这里可以在请求服务器之前拿到 [Request], 做一些操作比如给 [Request] 统一添加 token 或者 header 以及参数加密等操作
     *
     * @param chain   [okhttp3.Interceptor.Chain]
     * @param request [Request]
     * @return [Request]
     */
    @NonNull
    fun onHttpRequestBefore(@NonNull chain: Interceptor.Chain, @NonNull request: Request): Request

    fun redirectRequest(
        @NonNull chain: Interceptor.Chain,
        @NonNull request: Request,
        exception: Exception
    ): Response?

    /**
     * 空实现，一般需要自定义配置
     */
    val EMPTY: GlobalHttpHandler
        get() = object : GlobalHttpHandler {
            override fun onHttpResultResponse(
                httpResult: String?,
                chain: Interceptor.Chain,
                response: Response
            ): Response {
                return response
            }

            override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
                return request
            }

            override fun redirectRequest(
                chain: Interceptor.Chain,
                request: Request,
                exception: Exception
            ): Response? {
                return null
            }
        }
}
