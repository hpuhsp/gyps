package com.hnsh.core.http.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.hnsh.core.http.cache.LocalShareResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
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
