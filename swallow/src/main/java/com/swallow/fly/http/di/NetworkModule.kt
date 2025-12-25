package com.swallow.fly.http.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.swallow.fly.BuildConfig
import com.swallow.fly.base.lifecycle.config.FrameworkConfigHolder
import com.swallow.fly.http.CoroutineCallAdapterFactory
import com.swallow.fly.http.ResponseErrorListener
import com.swallow.fly.http.TimeoutCallAdapterFactory
import com.swallow.fly.http.interceptor.GlobalHttpHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @Description: 网络层依赖注入模块
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 使用 FrameworkConfigHolder，核心逻辑在 swallow 库
 *
 * 职责：
 * 1. 提供 Retrofit、OkHttpClient、Gson 实例
 * 2. 支持 RetrofitUrlManager（动态切换 BaseUrl）
 * 3. 通过 FrameworkConfigHolder 获取配置（app 模块无需 Hilt 注解）
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT = 30L
    private const val DEFAULT_BASE_URL = "https://api.github.com/"

    /**
     * 提供 BaseUrl
     * ✅ 优雅的实现：通过 FrameworkConfigHolder 获取配置
     */
    @Singleton
    @Provides
    fun provideBaseUrl(@ApplicationContext context: Context): HttpUrl {
        val builder = NetworkConfigBuilder()
        val config = FrameworkConfigHolder.getConfig()
        config.networkConfig(context, builder)
        return builder.baseUrl ?: DEFAULT_BASE_URL.toHttpUrlOrNull()!!
    }

    /**
     * 提供 GlobalHttpHandler
     */
    @Singleton
    @Provides
    fun provideGlobalHttpHandler(@ApplicationContext context: Context): GlobalHttpHandler? {
        val builder = NetworkConfigBuilder()
        val config = FrameworkConfigHolder.getConfig()
        config.networkConfig(context, builder)
        return builder.handler
    }

    /**
     * 提供 ResponseErrorListener
     */
    @Singleton
    @Provides
    fun provideResponseErrorListener(@ApplicationContext context: Context): ResponseErrorListener {
        val builder = NetworkConfigBuilder()
        val config = FrameworkConfigHolder.getConfig()
        config.networkConfig(context, builder)

        return builder.responseErrorListener ?: object : ResponseErrorListener {
            override fun handleResponseError(t: Throwable?): Throwable {
                return RuntimeException(t)
            }
        }
    }

    /**
     * 提供 Gson
     */
    @Singleton
    @Provides
    fun provideGson(@ApplicationContext context: Context): Gson {
        val gsonBuilder = GsonBuilder()
        val configBuilder = NetworkConfigBuilder()

        val config = FrameworkConfigHolder.getConfig()
        config.networkConfig(context, configBuilder)
        configBuilder.gsonConfiguration?.configGson(context, gsonBuilder)

        return gsonBuilder.create()
    }

    /**
     * 提供 OkHttpClient
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        @HandlerRequestInterceptor intercept: Interceptor,
        handler: GlobalHttpHandler?
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)

        // ✅ 支持 RetrofitUrlManager（动态切换 BaseUrl）
        RetrofitUrlManager.getInstance().with(builder)

        // 添加全局请求拦截器
        handler?.let { h ->
            builder.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = h.onHttpRequestBefore(chain, chain.request())
                    return chain.proceed(request)
                }
            })
        }

        // 添加请求拦截器
        builder.addInterceptor(intercept)

        // 添加日志拦截器（仅 Debug）
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        // ✅ 应用自定义配置
        val configBuilder = NetworkConfigBuilder()
        val config = FrameworkConfigHolder.getConfig()
        config.networkConfig(context, configBuilder)
        configBuilder.okhttpConfiguration?.configOkhttp(context, builder)

        return builder.build()
    }

    /**
     * 提供 Retrofit
     */
    @Singleton
    @Provides
    fun provideRetrofit(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        baseUrl: HttpUrl,
        gson: Gson
    ): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)

        // ✅ 应用自定义配置
        val configBuilder = NetworkConfigBuilder()
        val config = FrameworkConfigHolder.getConfig()
        config.networkConfig(context, configBuilder)
        configBuilder.retrofitConfiguration?.configRetrofit(context, builder)

        // 添加转换器和适配器
        builder
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addCallAdapterFactory(TimeoutCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

        return builder.build()
    }
}
