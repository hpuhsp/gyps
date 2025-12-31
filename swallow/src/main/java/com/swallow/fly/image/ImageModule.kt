package com.swallow.fly.image

import android.content.Context
import com.swallow.fly.base.lifecycle.config.FrameworkConfigHolder
import com.swallow.fly.base.lifecycle.parse.ManifestParser
import com.swallow.fly.http.di.ImageLoaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import javax.inject.Singleton

/**
 * @Description: 图片加载依赖注入模块
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的 Builder + 优先级控制
 *
 * 职责：只负责提供图片加载相关配置
 */
@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    /**
     * 提供图片加载拦截器
     * 用于 Glide 使用 OkHttp 方式加载图片时的自定义拦截
     * ✅ 使用合并后的配置
     */
    @Singleton
    @Provides
    @ImageLoaderInterceptor
    fun provideImageLoaderInterceptor(@ApplicationContext context: Context): Interceptor? {
        val builder = ImageConfigBuilder()
        FrameworkConfigHolder.applyImageConfig(context, builder)
        return builder.imageLoaderInterceptor
    }
}
