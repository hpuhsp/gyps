package com.hnsh.core.http

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.hnsh.core.ext.logd
import com.hnsh.core.http.interceptor.GlobalHttpHandler
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception
import java.lang.reflect.Type
import javax.inject.Inject

/**
 * @Description: 处理 Http 请求和响应结果（可配置请求头、添加用户Token等预处理操作）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/29 14:46
 * @UpdateRemark:   更新说明：
 */
class GlobalHttpHandlerImpl @Inject constructor(val context: Context) :
    GlobalHttpHandler {

    /**
     * 这里可以先客户端一步拿到每一次 Http 请求的结果, 可以先解析成 Json, 再做一些操作, 如检测到 token 过期后
     * 重新请求 token, 并重新执行请求
     *
     * @param httpResult 服务器返回的结果 (已被框架自动转换为字符串)
     * @param chain
     * @param response
     * @return
     */
    override fun onHttpResultResponse(
        httpResult: String?,
        chain: Interceptor.Chain,
        response: Response
    ): Response {

        /* 这里如果发现 token 过期, 可以先请求最新的 token, 然后在拿新的 token 放入 Request 里去重新请求
        注意在这个回调之前已经调用过 proceed(), 所以这里必须自己去建立网络请求, 如使用 Okhttp 使用新的 Request 去请求
        create a new request and modify it accordingly using the new token
        Request newRequest = chain.request().newBuilder().header("token", newToken)
                             .build();

        retry the request

        response.body().close();
        如果使用 Okhttp 将新的请求, 请求成功后, 再将 Okhttp 返回的 Response return 出去即可
        如果不需要返回新的结果, 则直接把参数 response 返回出去即可*/
        return response
    }

    /**
     * 这里可以在请求服务器之前拿到 {@link Request}, 做一些操作比如给 {@link Request} 统一添加 token 或者 header 以及参数加密等操作
     *
     * @param chain
     * @param request
     * @return
     */
    override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
        return chain.request().newBuilder()
            .header("content-type", "application/json;charset=UTF-8")
            .addHeader("platform", "Android")
//            .addHeader("model", SystemUtils.getDeviceInfo())
//            .addHeader("version", SystemUtils.getVersionName(context))
//            .addHeader("x-auth-token", MyApplication.getToken())
//            .addHeader("udid", MyApplication.getUDID())
            .build()
    }

    override fun redirectRequest(
        chain: Interceptor.Chain,
        request: Request,
        exception: Exception
    ): Response? {
        return null
    }
}