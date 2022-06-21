package com.swallow.fly.http.di

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.swallow.fly.base.app.AppModule
import com.swallow.fly.http.CoroutineCallAdapterFactory
import com.swallow.fly.http.TimeoutCallAdapterFactory
import com.swallow.fly.http.interceptor.GlobalHttpHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * @Description: 网络库通用配置封装
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 16:03
 * @UpdateRemark:   更新说明：
 */
@Module
@InstallIn(ApplicationComponent::class)
object ClientModule {
    private val TIME_OUT = 10L
    
    @Singleton
    @Provides
    fun provideRetrofit(
        application: Application,
        @Nullable configuration: RetrofitConfiguration?,
        builder: Retrofit.Builder,
        client: OkHttpClient,
        httpUrl: HttpUrl,
        goon: Gson
    ): Retrofit {
        builder
            .baseUrl(httpUrl)
            .client(client)
        configuration?.configRetrofit(application, builder)
        builder
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addCallAdapterFactory(TimeoutCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(goon))
        return builder.build()
    }
    
    @Singleton
    @Provides
    fun provideClient(
        application: Application,
        @Nullable configuration: OkhttpConfiguration?,
        builder: OkHttpClient.Builder,
        @HandlerRequestInterceptor intercept: Interceptor,
//        @Nullable interceptors: List<Interceptor>?,
        @Nullable handler: GlobalHttpHandler?
    ): OkHttpClient {
        builder
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        RetrofitUrlManager.getInstance().with(builder)
        if (handler != null)
            builder.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    return chain.proceed(handler.onHttpRequestBefore(chain, chain.request()))
                }
            })
        builder.addInterceptor(intercept)
        //如果外部提供了 Interceptor 的集合则遍历添加
//        if (!interceptors.isNullOrEmpty()) {
//            for (item in interceptors) {
//                builder.addInterceptor(item)
//            }
//        }
        
        configuration?.configOkhttp(application, builder)
        return RetrofitUrlManager.getInstance().with(builder)
            .build()
    }
    
    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
    }
    
    @Singleton
    @Provides
    fun provideClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }
    
    @Singleton
    @Provides
    fun provideGson(
        application: Application,
        @Nullable configuration: AppModule.GsonConfiguration?
    ): Gson {
        val builder = GsonBuilder()
        configuration?.configGson(application, builder)
        return builder.create()
    }
    
    /**
     * [Retrofit] 自定义配置接口
     */
    interface RetrofitConfiguration {
        fun configRetrofit(
            @NonNull context: Context?,
            @NonNull builder: Retrofit.Builder?
        )
    }
    
    /**
     * [OkHttpClient] 自定义配置接口
     */
    interface OkhttpConfiguration {
        fun configOkhttp(
            @NonNull context: Context,
            @NonNull builder: OkHttpClient.Builder
        )
    }
}