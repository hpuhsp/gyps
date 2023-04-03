package com.swallow.gyps.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.swallow.fly.http.di.ImageLoaderInterceptor
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.InputStream


/**
 * @Description: Glide全局配置
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/21 16:51
 * @UpdateRemark:   更新说明：
 */
@GlideModule
class GypsGlideModule : AppGlideModule() {
    
    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface ImageLoaderInterceptorEntryPoint {
        @ImageLoaderInterceptor
        fun getImageLoaderInterceptor(): Interceptor?
    }
    
    /**
     * Using the @GlideModule annotation requires a dependency on Glide’s annotations:
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // 替换组件，如网络请求组件(通过添加必要依赖实现:glide-loader-okhttp3)
        val entryPoint =
            EntryPointAccessors.fromApplication(
                context,
                ImageLoaderInterceptorEntryPoint::class.java
            )
        val interceptor = entryPoint.getImageLoaderInterceptor()
        if (null != interceptor) {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(interceptor)
            val okHttpClient: OkHttpClient = builder.build()
            registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(okHttpClient)
            )
        }
    }
    
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(
                context,
                diskCacheFolderName(context),
                diskCacheSizeBytes()
            )
        )
            .setMemoryCache(LruResourceCache(memoryCacheSizeBytes().toLong()))
    }
    
    /**
     * Implementations should return `false` after they and their dependencies have migrated
     * to Glide's annotation processor.
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
    
    /**
     * set the memory cache size, unit is the [Byte].
     */
    private fun memoryCacheSizeBytes(): Int {
        return 1024 * 1024 * 20 // 20 MB
    }
    
    /**
     * set the disk cache size, unit is the [Byte].
     */
    private fun diskCacheSizeBytes(): Long {
        return 1024 * 1024 * 512 // 512 MB
    }
    
    /**
     * set the disk cache folder's name.
     */
    private fun diskCacheFolderName(context: Context): String {
        return "Sh_Digital"
    }
}