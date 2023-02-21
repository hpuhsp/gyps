package com.swallow.fly.http.interceptor

import com.swallow.fly.http.di.ClientModule
import com.swallow.fly.http.di.DynamicTimeout
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.util.concurrent.TimeUnit


/**
 * @Description:  超时配置拦截器
 * @Author:  lfc
 * @Email:    iamlifuchang@163.com
 * @CreateTime:     2023/2/21 10:45
 * @UpdateRemark:   超时配置拦截器
 */
class TimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            val reequest: Request = chain.request()

            //核心代码!!!
            //核心代码!!!
            val tag = request.tag(Invocation::class.java)
            val timeout: DynamicTimeout? =
                tag?.method()?.getAnnotation(DynamicTimeout::class.java)


            return chain.withConnectTimeout(timeout?.timeout ?: 10, TimeUnit.SECONDS)
                .withReadTimeout(timeout?.timeout ?: 10, TimeUnit.SECONDS)
                .proceed(reequest)
//            return chain.proceed(reequest)
        } catch (e: Exception) {
            e.printStackTrace()
            return chain.proceed(request);

        }


    }
}