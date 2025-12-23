package com.swallow.fly.base.app.config

import android.content.Context
import com.google.gson.GsonBuilder
import com.swallow.fly.base.app.AppLifecycle
import com.swallow.fly.base.app.ConfigModule
import com.swallow.fly.db.DatabaseConfigBuilder
import com.swallow.fly.http.ResponseErrorListenerImpl
import com.swallow.fly.http.di.GsonConfiguration
import com.swallow.fly.http.di.NetworkConfigBuilder
import com.swallow.fly.http.di.OkhttpConfiguration
import com.swallow.fly.image.ImageConfigBuilder
import com.swallow.fly.log.LogConfigBuilder
import okhttp3.OkHttpClient

/**
 * @Description: 全局基本配置（示例）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 16:31
 * @UpdateRemark:   现代化升级 - 类型安全的 Builder + 优先级控制
 * 
 * 这是一个示例配置，展示如何使用类型安全的 Builder 配置框架
 * 实际项目中，应该在 app 模块创建自己的 GlobalConfiguration
 */
class GlobalConfiguration : ConfigModule {

    /**
     * 配置优先级
     * swallow 模块的默认配置优先级最低
     */
    override fun priority(): Int = 0

    /**
     * 配置网络层
     */
    override fun configureNetwork(context: Context, builder: NetworkConfigBuilder) {
        // 配置默认域名
        builder.baseUrl("https://api.github.com/")
        
        // 配置错误监听器
        builder.responseErrorListener(ResponseErrorListenerImpl(context))
        
        // 配置 Gson
        builder.gsonConfiguration(object : GsonConfiguration {
            override fun configGson(context: Context?, builder: GsonBuilder?) {
                builder?.serializeNulls()
                    ?.enableComplexMapKeySerialization()
            }
        })
        
        // 配置 OkHttp
        builder.okhttpConfiguration(object : OkhttpConfiguration {
            override fun configOkhttp(context: Context, builder: OkHttpClient.Builder) {
                // 可以在这里添加自定义拦截器
            }
        })
    }

    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {
        // 可以在这里注入模块生命周期
    }
}