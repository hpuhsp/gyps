package com.swallow.fly.http.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.swallow.fly.http.cache.LocalShareResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideUserInfoRepository(sharedPreferences: SharedPreferences): LocalShareResource {
        return LocalShareResource.getInstance(sharedPreferences)
    }
    
    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("SH_omp", Context.MODE_PRIVATE)
    }
}
