package com.swallow.fly.http.di

import javax.inject.Qualifier

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2022/6/21 9:12
 * @UpdateRemark:   更新说明：
 */

/**
 * Glide集成OkHttp库自定义拦截器
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageLoaderInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HandlerRequestInterceptor