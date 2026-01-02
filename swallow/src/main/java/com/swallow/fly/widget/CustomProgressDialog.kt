package com.swallow.fly.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.swallow.fly.R
import com.swallow.fly.base.ui.activity.IActivity
import com.swallow.fly.ext.logd

/**
 * @Description: 支持自定义与原生风格的 Loading Dialog
 * @Author: Hsp
 */
class CustomProgressDialog(private val originContext: Context) : AlertDialog(originContext) {

    private var customView: View =
        LayoutInflater.from(originContext).inflate(R.layout.base_loading_progress, null)

    // Views
    private var llCustomStyle: View? = null
    private var tvCustomMsg: TextView? = null

    private var cvSystemStyle: View? = null
    private var tvSystemMsg: TextView? = null

    init {
        // 虽然继承自 AlertDialog，但我们通过 setView 来使用自定义布局
        // 注意：AlertDialog 的构造通常需要 theme，这里依赖默认 theme 或者 manifest 设置
        setView(customView)

        // 透明背景，去除默认框
        window?.setBackgroundDrawable(ColorDrawable(0))

        initViews()
        checkStyle()
    }

    private fun initViews() {
        llCustomStyle = customView.findViewById(R.id.ll_custom_style)
        tvCustomMsg = customView.findViewById(R.id.tv_loading_message_custom)

        cvSystemStyle = customView.findViewById(R.id.cv_system_style)
        tvSystemMsg = customView.findViewById(R.id.tv_loading_message_system)
    }

    /**
     * 根据 Context 配置决定显示哪种风格
     */
    private fun checkStyle() {
        val useSystem = (originContext as? IActivity)?.showSystemProgress() ?: false
        if (useSystem) {
            // 系统风格
            llCustomStyle?.visibility = View.GONE
            cvSystemStyle?.visibility = View.VISIBLE
        } else {
            // 自定义风格
            llCustomStyle?.visibility = View.VISIBLE
            cvSystemStyle?.visibility = View.GONE
        }
    }

    /**
     * 设置显示文案
     */
    override fun setMessage(message: CharSequence?) {
        val msg = if (message.isNullOrEmpty()) "加载中..." else message
        tvCustomMsg?.text = msg
        tvSystemMsg?.text = msg
    }
}