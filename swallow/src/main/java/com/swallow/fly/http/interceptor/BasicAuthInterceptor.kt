package com.swallow.fly.http.interceptor

import android.util.Base64
import com.swallow.fly.http.cache.LocalShareResource
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Description:
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/8/22 9:16
 * @UpdateRemark:
 */
class BasicAuthInterceptor(
    private val userRepository: LocalShareResource
) : Interceptor {
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRlIjoxNTQzMTk1MjEyMzQ1LCJlbXBsb3llZUlkIjoiMTAyMiIsImVtcGxveWVlTmFtZSI6Iuasp-mYs-mUiyIsImVudGVycHJpc2VJZCI6IjE5MyIsImxvY2F0aW9uIjoi5rKz5Y2X55yBIiwidXNlckNvZGUiOiIxMzc4MzA4NTAwMyIsInVzZXJJZCI6IjEwMTQ4IiwidXNlck5hbWUiOiLmrKfpmLPplIsifQ==.064e9150d89d909412f8582b6cbb621a690ab7a3c19d4328476937350df71db9"

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = getAuthorization()

        if (accessToken.isNotEmpty()) {
            val url = request.url.toString()
            request = request.newBuilder()
                .header("content-type", "application/json;charset=UTF-8")
//                .addHeader("Authorization", accessToken)
//                .addHeader("platform", "Android")
//                .addHeader("model", SystemUtils.getDeviceInfo())
//                .addHeader("version", SystemUtils.getVersionName(context))
//                .addHeader("x-auth-token", accessToken)
//                .addHeader("udid", MyApplication.getUDID())
//                .addHeader("token", token)
                .url(url)
                .build()
        }

        return chain.proceed(request)
    }

    private fun getAuthorization(): String {
        val accessToken = userRepository.accessToken
        val username = userRepository.username
        val password = userRepository.password

        if (accessToken.isBlank()) {
            val basicIsEmpty = username.isBlank() || password.isBlank()
            return if (basicIsEmpty) {
                ""
            } else {
                "$username:$password".let {
                    "basic " + Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP)
                }
            }
        }
        return "token $accessToken"
    }
}