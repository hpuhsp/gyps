package com.hnsh.core.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import com.hnsh.core.R

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/2 17:04
 * @UpdateRemark:   更新说明：
 */
class DoubleClickExitHelper {
    private var mActivity: Activity? = null
    private var isOnKeyBacking = false
    private var mHandler: Handler? = null
    private var mBackToast: Toast? = null

    constructor(activity: Activity?) {
        mActivity = activity
        mHandler = Handler(Looper.getMainLooper())
    }

    /**
     * Activity onKeyDown事件
     */
    fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?,
        cls: Class<*>?
    ): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false
        }
        return if (isOnKeyBacking) {
            mHandler!!.removeCallbacks(onBackTimeRunnable)
            if (mBackToast != null) {
                mBackToast!!.cancel()
            }
            // SingleTask方式退出
            mActivity?.finish()
            // 暂未实现下面的方式
//            AppManager.getInstance().killActivity(cls)
            true
        } else {
            isOnKeyBacking = true
            if (mBackToast == null) {
                mBackToast = Toast.makeText(
                    mActivity?.applicationContext,
                    R.string.double_click_exit,
                    Toast.LENGTH_SHORT
                )
            }
            mBackToast?.show()
            mHandler?.postDelayed(onBackTimeRunnable, 2000)
            true
        }
    }

    private val onBackTimeRunnable = Runnable {
        isOnKeyBacking = false
        if (mBackToast != null) {
            mBackToast?.cancel()
        }
    }
}