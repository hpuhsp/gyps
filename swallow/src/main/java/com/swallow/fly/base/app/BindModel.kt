package com.swallow.fly.base.app

import com.swallow.fly.http.interceptor.RequestInterceptor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 18:14
 * @UpdateRemark:   更新说明：
 */
@Module
@InstallIn(ApplicationComponent::class)
abstract class BindModel {
    @Binds
    abstract fun bindInterceptor(interceptor: RequestInterceptor): Interceptor
}