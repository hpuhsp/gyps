package com.swallow.fly.http

import android.content.Context
import com.swallow.fly.http.interceptor.GlobalHttpHandler
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * 全局 HTTP 请求处理器实现 - 现代化版本
 * 
 * 处理 HTTP 请求和响应结果，可配置请求头、添加用户 Token 等预处理操作
 * 
 * @Description: 处理 Http 请求和响应结果（可配置请求头、添加用户Token等预处理操作）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/29 14:46
 * @UpdateRemark: 2024 - 现代化升级：优化代码结构，添加日志，改进错误处理
 */
class GlobalHttpHandlerImpl @Inject constructor(
    private val context: Context
) : GlobalHttpHandler {

    /**
     * 处理 HTTP 响应结果
     * 
     * 可以在这里：
     * - 检测 token 过期并重新请求
     * - 统一处理业务错误码
     * - 记录响应日志
     * 
     * @param httpResult 服务器返回的结果（已被框架自动转换为字符串）
     * @param chain 拦截器链
     * @param response 响应对象
     * @return 处理后的响应对象
     */
    override fun onHttpResultResponse(
        httpResult: String?,
        chain: Interceptor.Chain,
        response: Response
    ): Response {
        // 记录响应信息
        if (!response.isSuccessful) {
            Timber.w("HTTP Response Error: code=${response.code}, message=${response.message}")
        }
        
        /* 
         * Token 过期处理示例：
         * 
         * if (httpResult?.contains("token_expired") == true) {
         *     // 1. 请求新的 token
         *     val newToken = refreshToken()
         *     
         *     // 2. 使用新 token 重新构建请求
         *     val newRequest = chain.request().newBuilder()
         *         .header("Authorization", "Bearer $newToken")
         *         .build()
         *     
         *     // 3. 关闭旧响应
         *     response.body?.close()
         *     
         *     // 4. 重新执行请求
         *     return chain.proceed(newRequest)
         * }
         */
        
        return response
    }

    /**
     * 请求前预处理
     * 
     * 可以在这里：
     * - 统一添加请求头
     * - 添加 Token 认证
     * - 添加设备信息
     * - 参数加密
     * 
     * @param chain 拦截器链
     * @param request 原始请求
     * @return 处理后的请求
     */
    override fun onHttpRequestBefore(
        chain: Interceptor.Chain,
        request: Request
    ): Request {
        return request.newBuilder().apply {
            // 设置 Content-Type
            header("Content-Type", "application/json;charset=UTF-8")
            
            // 添加平台标识
            addHeader("Platform", "Android")
            
            // 添加设备信息（可选）
            // addHeader("Device-Model", Build.MODEL)
            // addHeader("Device-Brand", Build.BRAND)
            // addHeader("OS-Version", Build.VERSION.RELEASE)
            
            // 添加应用版本（可选）
            // addHeader("App-Version", BuildConfig.VERSION_NAME)
            // addHeader("App-Version-Code", BuildConfig.VERSION_CODE.toString())
            
            // 添加认证 Token（可选）
            // val token = getAuthToken()
            // if (!token.isNullOrEmpty()) {
            //     addHeader("Authorization", "Bearer $token")
            // }
            
            // 添加设备唯一标识（可选）
            // val deviceId = getDeviceId()
            // if (!deviceId.isNullOrEmpty()) {
            //     addHeader("Device-ID", deviceId)
            // }
        }.build()
    }

    /**
     * 请求失败时的重定向处理
     * 
     * 可以在这里：
     * - 处理网络异常
     * - 实现请求重试逻辑
     * - 返回备用响应
     * 
     * @param chain 拦截器链
     * @param request 原始请求
     * @param exception 异常信息
     * @return 重定向后的响应，如果返回 null 则抛出原始异常
     */
    override fun redirectRequest(
        chain: Interceptor.Chain,
        request: Request,
        exception: Exception
    ): Response? {
        // 记录异常
        Timber.e(exception, "HTTP Request Failed: ${request.url}")
        
        /*
         * 重试逻辑示例：
         * 
         * if (exception is SocketTimeoutException && retryCount < MAX_RETRY) {
         *     retryCount++
         *     return chain.proceed(request)
         * }
         */
        
        // 返回 null 表示不处理，将抛出原始异常
        return null
    }
    
    companion object {
        private const val TAG = "GlobalHttpHandler"
    }
}