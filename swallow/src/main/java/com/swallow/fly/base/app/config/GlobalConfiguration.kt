package com.swallow.fly.base.app.config

import android.content.Context
import com.google.gson.GsonBuilder
import com.swallow.fly.BuildConfig
import com.swallow.fly.base.app.AppLifecycle
import com.swallow.fly.base.app.AppModule
import com.swallow.fly.base.app.ConfigModule
import com.swallow.fly.http.ResponseErrorListenerImpl
import com.swallow.fly.http.di.ClientModule
import com.swallow.fly.http.interceptor.RequestInterceptor
import okhttp3.OkHttpClient

/**
 * @Description: 全局基本配置
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 16:31
 * @UpdateRemark:   更新说明：
 */
class GlobalConfiguration : ConfigModule {

    override fun applyOptions(context: Context?, builder: GlobalConfigModule.Builder) {
        if (!BuildConfig.LOG_DEBUG) //Release 时,让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE)
        // 配置默认域名
        builder.baseurl("https://api.github.com")
//            .globalHttpHandler(context?.let { GlobalHttpHandlerImpl(it) })
            .responseErrorListener(context?.let { ResponseErrorListenerImpl(it) })
            .gsonConfiguration(object : AppModule.GsonConfiguration {
                override fun configGson(context: Context?, builder: GsonBuilder?) {
                    //这里可以自己自定义配置Gson的参数，支持将序列化key为object的map,默认只能序列化key为string的map
                    builder?.serializeNulls()
                        ?.enableComplexMapKeySerialization()
                }
            })
            .okhttpConfiguration(object : ClientModule.OkhttpConfiguration {
                override fun configOkhttp(context: Context, builder: OkHttpClient.Builder) {
//                    RetrofitUrlManager.getInstance().with(builder)
                }
            })
    }

    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {

    }
}