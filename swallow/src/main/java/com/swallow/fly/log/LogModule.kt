package com.swallow.fly.log

import android.content.Context
import com.swallow.fly.base.lifecycle.config.SwallowConfig
import com.swallow.fly.http.interceptor.RequestInterceptor
import com.swallow.fly.http.printer.DefaultFormatPrinter
import com.swallow.fly.http.printer.FormatPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Description: 日志依赖注入模块
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的 Builder + 优先级控制
 *
 * 职责：只负责提供日志相关配置
 */
@Module
@InstallIn(SingletonComponent::class)
object LogModule {

    /**
     * 提供 HTTP 日志打印级别
     * ✅ 使用合并后的配置
     */
    @Singleton
    @Provides
    fun providePrintHttpLogLevel(config: SwallowConfig): RequestInterceptor.Level {
        return config.log.printHttpLogLevel ?: RequestInterceptor.Level.ALL
    }

    /**
     * 提供格式化打印器
     * ✅ 使用合并后的配置
     */
    @Singleton
    @Provides
    fun provideFormatPrinter(config: SwallowConfig): FormatPrinter {
        return config.log.formatPrinter ?: DefaultFormatPrinter()
    }
}
