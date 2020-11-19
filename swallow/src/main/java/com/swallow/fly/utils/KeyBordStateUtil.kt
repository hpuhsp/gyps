package com.swallow.fly.utils

import android.R
import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/2 17:00
 * @UpdateRemark:   更新说明：
 */
class KeyBordStateUtil {
    private var listener: OnKeyBordStateListener? = null
    private var rootLayout: View? = null
    private var mVisibleHeight = 0
    private var mFirstVisibleHeight = 0

    constructor(context: Activity) {
        rootLayout = (context.findViewById<View>(R.id.content) as ViewGroup).getChildAt(0)
        rootLayout?.viewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)
    }

    private val mOnGlobalLayoutListener: OnGlobalLayoutListener? =
        OnGlobalLayoutListener { calKeyBordState() }

    private fun calKeyBordState() {
        val r = Rect()
        rootLayout!!.getWindowVisibleDisplayFrame(r)
        val visibleHeight = r.height()
        if (mVisibleHeight == 0) {
            mVisibleHeight = visibleHeight
            mFirstVisibleHeight = visibleHeight
            return
        }
        if (mVisibleHeight == visibleHeight) {
            return
        }
        mVisibleHeight = visibleHeight
        val mIsKeyboardShow: Boolean = mVisibleHeight < mFirstVisibleHeight
        if (mIsKeyboardShow) {
            //键盘高度
            val keyboardHeight: Int = Math.abs(mVisibleHeight - mFirstVisibleHeight)
            if (listener != null) {
                listener!!.onSoftKeyBoardShow(keyboardHeight)
            }
        } else {
            if (listener != null) {
                listener!!.onSoftKeyBoardHide()
            }
        }
    }

    fun addOnKeyBordStateListener(listener: OnKeyBordStateListener?) {
        this.listener = listener
    }

    fun removeOnKeyBordStateListener() {
        if (rootLayout != null && mOnGlobalLayoutListener != null) {
            rootLayout!!.viewTreeObserver.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
        }
        if (listener != null) {
            listener = null
        }
    }

    interface OnKeyBordStateListener {
        /**
         * 键盘显示
         *
         * @param keyboardHeight
         */
        fun onSoftKeyBoardShow(keyboardHeight: Int)

        /**
         * 键盘隐藏
         */
        fun onSoftKeyBoardHide()
    }
}