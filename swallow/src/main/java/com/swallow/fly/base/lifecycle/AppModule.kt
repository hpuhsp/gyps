package com.swallow.fly.base.lifecycle

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import com.google.gson.GsonBuilder
import com.swallow.fly.http.ResponseErrorListener
import com.swallow.fly.http.engine.HttpEngine
import com.swallow.fly.http.manager.RepositoryManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Description: 应用级别依赖注入模块
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 10:20
 * @UpdateRemark:   
 *   - 2026/01: 更新为使用 HttpEngine 而非直接依赖 Retrofit
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * 提供 RepositoryManager 实例
     * 
     * 注意：现在依赖 HttpEngine 而非 Retrofit
     * 这样可以支持多种网络库实现
     */
    @Provides
    @Singleton
    fun provideRepositoryManager(
        application: Application,
        httpEngine: HttpEngine,
        errorListener: ResponseErrorListener
    ): RepositoryManager {
        return RepositoryManager(
            application,
            httpEngine,
            errorListener
        )
    }

    interface GsonConfiguration {
        fun configGson(
            @NonNull context: Context?,
            @NonNull builder: GsonBuilder?
        )
    }
}