package com.swallow.fly.base.view

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.swallow.fly.base.viewmodel.BaseViewModel

/**
 * @Description: 懒加载模式
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/10/23 13:33
 * @UpdateRemark:   更新说明：
 */
abstract class BaseLazyFragment<VM : BaseViewModel, VB : ViewBinding> :
    BaseFragment<VM, VB>() {

    private var isFirstVisible: Boolean = true
    private var isPrepared: Boolean = false

    protected abstract fun onFirstVisibleToUser()

    protected abstract fun onVisibleToUser()

    protected abstract fun onInvisibleToUser()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPrepare()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstVisible) {
            initPrepare()
            isFirstVisible = false
        } else {
            onVisibleToUser();
        }
    }

    override fun onPause() {
        super.onPause()
        onInvisibleToUser()
    }

    @Synchronized
    private fun initPrepare() {
        if (isPrepared) {
            onFirstVisibleToUser()
        } else {
            isPrepared = true
        }
    }

    fun isFirstVisibleToUser(): Boolean {
        return isFirstVisible
    }
}