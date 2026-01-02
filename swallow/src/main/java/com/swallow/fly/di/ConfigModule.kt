package com.swallow.fly.di

import com.swallow.fly.base.lifecycle.config.FrameworkConfigHolder
import com.swallow.fly.base.lifecycle.config.SwallowConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    
    @Singleton
    @Provides
    fun provideSwallowConfig(): SwallowConfig {
        return FrameworkConfigHolder.provideConfig()
    }
}
