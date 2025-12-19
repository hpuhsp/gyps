package com.swallow.gyps.glide

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.decodeTypeOf


/**
 * @Description: Glide 工具类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 16:41
 * @UpdateRemark:   
 *   - 2024/12: 移除 @GlideExtension 注解，Glide 5.x 已弃用 Generated API
 *   - 改为普通工具方法
 */
object GlideOptions {
    // 用户头像
    private const val USER_AVATAR_SIZE = 168

    // 普通图大小
    private const val NORMAL_IMG_SIZE = 240

    /**
     * 用户头像加载选项
     */
    @JvmStatic
    fun avatarThumb(): RequestOptions {
        return RequestOptions()
            .fitCenter()
            .override(USER_AVATAR_SIZE)
    }

    /**
     * 加载普通图片选项
     */
    @JvmStatic
    fun normalThumb(): RequestOptions {
        return RequestOptions()
            .centerCrop()
            .placeholder(com.swallow.fly.R.drawable.img_default_normal_thumb)
            .error(com.swallow.fly.R.drawable.img_default_error_thumb)
            .override(NORMAL_IMG_SIZE)
    }
//
//    /**
//     * GIF 图片加载选项
//     */
//    @JvmStatic
//    fun gifOptions(): RequestOptions {
//        return decodeTypeOf(GifDrawable::class.java)
//            .apply(DrawableTransitionOptions())
//    }
}