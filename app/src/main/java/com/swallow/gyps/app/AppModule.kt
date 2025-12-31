package com.swallow.gyps.app

import com.swallow.fly.base.lifecycle.config.ModuleConfig
import com.swallow.fly.base.lifecycle.config.ModuleConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Description: App module Hilt configuration
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/26
 * @UpdateRemark:   Provides empty ModuleConfigProvider set for Hilt injection
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides an empty set of ModuleConfigProvider
     * This satisfies the @Inject requirement in BaseApplication
     */
    @Provides
    @Singleton
    @ModuleConfig
    fun provideModuleConfigProviders(): Set<@JvmSuppressWildcards ModuleConfigProvider> {
        return emptySet()
    }
}
