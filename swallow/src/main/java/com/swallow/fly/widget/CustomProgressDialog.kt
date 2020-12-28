package com.swallow.fly.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.blankj.utilcode.util.ScreenUtils
import com.swallow.fly.R

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/26 10:10
 * @UpdateRemark:   更新说明：
 */
class CustomProgressDialog(context: Context) :
    AppCompatDialog(context) {
    private var customView: View =
        LayoutInflater.from(context).inflate(R.layout.base_loading_progress, null)
    private var tvDesc: TextView
    private var showDesc = false

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(customView)
        val window = window
        if (null != window) {
            val dialogWidth = (ScreenUtils.getScreenWidth() * 0.35f).toInt()
            window.setGravity(Gravity.CENTER)
            window.setLayout(dialogWidth, dialogWidth)
            window.setDimAmount(0f)
        }

        this.setCancelable(true)
        this.setCanceledOnTouchOutside(true)
        this.setCancelable(true)
        this.window?.setBackgroundDrawable(ColorDrawable(0x000000))
        tvDesc = customView.findViewById(R.id.tipTextView)
    }

    /**
     * 设置显示文案
     */
    open fun setMessage(message: String?) {
        showDesc = true
        tvDesc.visibility = View.VISIBLE
        tvDesc.text = if (message.isNullOrEmpty()) "正在加载..." else message
    }
}