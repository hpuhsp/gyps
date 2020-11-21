package com.hsp.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/10/5 11:35
 * @UpdateRemark:   更新说明：
 */
class MyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    var userInputEnabled = true
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (userInputEnabled) {
            try {
                return super.onInterceptTouchEvent(ev)
            } catch (e: IllegalArgumentException) {
                Log.e("TAG", e.message ?: "")
            }
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (userInputEnabled) {
            try {
                return super.onTouchEvent(ev)
            } catch (e: IllegalArgumentException) {
                Log.e("TAG", e.message ?: "")
            }
        }
        return false
    }
}