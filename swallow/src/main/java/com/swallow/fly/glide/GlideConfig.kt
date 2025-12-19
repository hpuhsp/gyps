package com.swallow.fly.glide

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestOptions

/**
 * Glide 配置辅助对象
 * 提供常用的图片变换和配置选项
 */
object GlideConfig {

    /**
     * 默认配置
     */
    fun defaultOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
    }

    /**
     * 圆角配置
     * @param radiusPx 圆角半径（像素）
     */
    fun roundedCornersOptions(radiusPx: Int): RequestOptions {
        return RequestOptions()
            .transform(CenterCrop(), RoundedCorners(radiusPx))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 圆形配置
     */
    fun circleOptions(): RequestOptions {
        return RequestOptions()
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 居中裁剪配置
     */
    fun centerCropOptions(): RequestOptions {
        return RequestOptions()
            .transform(CenterCrop())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 适应配置
     */
    fun fitCenterOptions(): RequestOptions {
        return RequestOptions()
            .transform(FitCenter())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 居中内部配置
     */
    fun centerInsideOptions(): RequestOptions {
        return RequestOptions()
            .transform(CenterInside())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 高质量配置
     */
    fun highQualityOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
    }

    /**
     * 低质量配置（快速加载）
     */
    fun lowQualityOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
    }

    /**
     * 仅缓存原图配置
     */
    fun cacheSourceOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    /**
     * 仅缓存转换后的图片配置
     */
    fun cacheResourceOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    }

    /**
     * 不缓存配置
     */
    fun noCacheOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
    }

    /**
     * 缩略图配置
     * @param sizeMultiplier 缩放比例（0.1 表示 10%）
     */
    fun thumbnailOptions(sizeMultiplier: Float = 0.1f): RequestOptions {
        return RequestOptions()
            .sizeMultiplier(sizeMultiplier)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 覆盖尺寸配置
     * @param width 宽度
     * @param height 高度
     */
    fun overrideOptions(width: Int, height: Int): RequestOptions {
        return RequestOptions()
            .override(width, height)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    /**
     * 组合多个变换
     */
    fun multiTransformOptions(vararg transforms: BitmapTransformation): RequestOptions {
        return RequestOptions()
            .transform(*transforms)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }
}
