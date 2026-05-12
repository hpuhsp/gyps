package com.swallow.fly.base.view

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.swallow.fly.base.viewmodel.BaseViewModel

/**
 * @Description: 懒加载 Fragment 基类
 * @Author:   Hsp
 * @UpdateRemark:
 *   - 移除废弃的 onActivityCreated，改用 onViewCreated + onStart/onStop
 *   - 修正懒加载竞态：视图就绪且首次可见时才触发 onFirstVisibleToUser
 */
abstract class BaseLazyFragment<VM : BaseViewModel, VB : ViewBinding> :
    BaseFragment<VM, VB>() {

    private var isViewCreated: Boolean = false
    private var isFirstVisible: Boolean = true

    protected abstract fun onFirstVisibleToUser()

    protected abstract fun onVisibleToUser()

    protected abstract fun onInvisibleToUser()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
    }

    override fun onStart() {
        super.onStart()
        if (!isViewCreated) return
        if (isFirstVisible) {
            isFirstVisible = false
            onFirstVisibleToUser()
        } else {
            onVisibleToUser()
        }
    }

    override fun onStop() {
        super.onStop()
        onInvisibleToUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        isFirstVisible = true
    }

    fun isFirstVisibleToUser(): Boolean {
        return isFirstVisible
    }
}