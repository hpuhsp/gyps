package com.swallow.fly.base.view

import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/8 10:10
 * @UpdateRemark:   更新说明：
 */
abstract class BaseAdapter<T : Any, VB : BaseViewHolder>(
    @LayoutRes private val layoutResId: Int,
    data: MutableList<T>? = null
) :
    BaseQuickAdapter<T, VB>(layoutResId, null) {
    init {

    }
}


