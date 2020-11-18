package com.hnsh.core.base.view

import android.os.Bundle

/**
 * @Description: 支持懒加载
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/10/23 13:33
 * @UpdateRemark:   更新说明：
 */
abstract class BaseLazyFragment : BaseFragment<Nothing>() {

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

    override fun useBinding(): Boolean {
        return false
    }

    fun isFirstVisibleToUser(): Boolean {
        return isFirstVisible
    }
}