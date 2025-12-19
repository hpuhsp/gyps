package com.swallow.fly.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * ImageView 扩展函数
 * 提供便捷的图片加载功能
 */

/**
 * 加载图片（基础版）
 *
 * @param url 图片 URL
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadImage(
    url: String?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载图片（带圆角）
 *
 * @param url 图片 URL
 * @param radius 圆角半径（dp）
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadImageWithRadius(
    url: String?,
    radius: Int,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    val radiusPx = context.dp2px(radius.toFloat())
    Glide.with(this)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
            transform(CenterCrop(), RoundedCorners(radiusPx))
        }
        .into(this)
}

/**
 * 加载圆形图片
 *
 * @param url 图片 URL
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadCircleImage(
    url: String?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
            transform(CircleCrop())
        }
        .into(this)
}

/**
 * 加载图片（从 Uri）
 */
fun ImageView.loadImage(
    uri: Uri?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(uri)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载图片（从 File）
 */
fun ImageView.loadImage(
    file: File?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(file)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载图片（从资源 ID）
 */
fun ImageView.loadImage(
    @DrawableRes resId: Int,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(resId)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载图片（带缓存策略）
 *
 * @param url 图片 URL
 * @param cacheStrategy 缓存策略
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadImageWithCache(
    url: String?,
    cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
            diskCacheStrategy(cacheStrategy)
        }
        .into(this)
}

/**
 * 加载缩略图
 *
 * @param url 图片 URL
 * @param thumbnail 缩略图比例（0.1 表示 10%）
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadThumbnail(
    url: String?,
    thumbnail: Float = 0.1f,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .load(url)
        .thumbnail(thumbnail)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载 GIF 图片
 */
fun ImageView.loadGif(
    url: String?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this)
        .asGif()
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载图片（自定义选项）
 *
 * @param url 图片 URL
 * @param options 自定义 RequestOptions
 */
fun ImageView.loadImageWithOptions(
    url: String?,
    options: RequestOptions
) {
    Glide.with(this)
        .load(url)
        .apply(options)
        .into(this)
}

/**
 * 清除图片
 */
fun ImageView.clearImage() {
    Glide.with(this).clear(this)
}

/**
 * 预加载图片
 */
fun ImageView.preloadImage(url: String?) {
    Glide.with(this)
        .load(url)
        .preload()
}
