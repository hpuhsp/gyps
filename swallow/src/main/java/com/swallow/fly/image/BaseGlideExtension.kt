package com.swallow.fly.image

import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.decodeTypeOf
import com.swallow.fly.R


/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 16:41
 * @UpdateRemark:   更新说明：
 */
@GlideExtension
open class BaseGlideExtension private constructor() {

    /**
     * 可基于此类进行通用模块的扩展
     * 特別注意：
     * 使用 @GlideOption 标记的方法应该为静态方法，并且返回值为 BaseRequestOptions<?>。
     * 这些生成的方法在标准的 Glide 和 RequestOptions 类里不可用，只存在于生成的等效类中。
     */
    companion object {
        // 用户头像
        private const val USER_AVATAR_SIZE = 168

        // 普通图大小
        private const val NORMAL_IMG_SIZE = 240

        @JvmStatic
        @GlideOption
        fun avatarThumb(options: BaseRequestOptions<*>): BaseRequestOptions<*> {

            return options
                .fitCenter()
                .override(USER_AVATAR_SIZE)
        }

        /**
         * 加载普通图片。默认使用内置过渡处理。
         */
        @JvmStatic
        @GlideOption
        fun normalThumb(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
            return options
                .centerCrop()
                .placeholder(R.drawable.img_default_normal_thumb)
                .error(R.drawable.img_default_error_thumb)
                .override(NORMAL_IMG_SIZE)
        }


        private val DECODE_TYPE_GIF: RequestOptions = decodeTypeOf(GifDrawable::class.java).lock()

//        /**
//         * 可使用GlideType注解扩展对新的资源类型的支持(GIF，SVG 等等)
//         */
//        @JvmStatic
//        @GlideType(GifDrawable::class)
//        fun asGifImage(requestBuilder: RequestBuilder<GifDrawable>): RequestBuilder<GifDrawable> {
//            return requestBuilder
//                .transition(DrawableTransitionOptions())
//                .apply(DECODE_TYPE_GIF)
//        }
    }
}