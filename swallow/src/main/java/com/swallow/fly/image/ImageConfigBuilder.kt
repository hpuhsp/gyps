package com.swallow.fly.image

import okhttp3.Interceptor

/**
 * @Description: 图片加载配置构建器
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的配置构建器
 * 
 * 使用示例：
 * ```kotlin
 * override fun configureImage(context: Context, builder: ImageConfigBuilder) {
 *     builder.imageLoaderInterceptor(object : Interceptor {
 *         override fun intercept(chain: Interceptor.Chain): Response {
 *             // 自定义拦截逻辑
 *             return chain.proceed(chain.request())
 *         }
 *     })
 * }
 * ```
 */
class ImageConfigBuilder {
    
    var imageLoaderInterceptor: Interceptor? = null
        private set
    
    /**
     * 配置图片加载拦截器
     * 
     * 用于 Glide 使用 OkHttp 方式加载图片时的自定义拦截
     * 
     * @param interceptor 拦截器
     * @return 当前构建器，支持链式调用
     */
    fun imageLoaderInterceptor(interceptor: Interceptor): ImageConfigBuilder {
        this.imageLoaderInterceptor = interceptor
        return this
    }
}
