package com.swallow.gyps.glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import com.swallow.fly.utils.SingletonHolderNoneArg

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/21 11:21
 * @UpdateRemark:   更新说明：
 */
class GlideEngine : ImageEngine {

    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView);
    }

    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView?,
        callback: OnImageCompleteCallback?
    ) {
    }

    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView?
    ) {
    }

    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
    }

    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
    }

    companion object :
        SingletonHolderNoneArg<GlideEngine>(::GlideEngine)
}