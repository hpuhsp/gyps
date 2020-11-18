package com.hnsh.core.base.app

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hnsh.core.base.app.AppDelegate
import com.hnsh.core.http.ResponseErrorListener
import com.hnsh.core.http.manager.IRepositoryManager
import com.hnsh.core.http.manager.RepositoryManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 10:20
 * @UpdateRemark:   更新说明：
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRepositoryManager(
        application: Application,
        retrofit: Retrofit,
        errorListener: ResponseErrorListener
    ): RepositoryManager {
        return RepositoryManager(
            application,
            retrofit,
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