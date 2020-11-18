package com.hnsh.core.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/25 14:24
 * @UpdateRemark:   更新说明：
 */
interface ViewBehavior {
    /**
     * 正在加载
     */
    fun showLoading(@Nullable msg: Int)

    /**
     * 显示确认弹框
     */
    fun showConfirmDialog(content: String)

    /**
     * 隱藏
     */
    fun hideAllDialog()

    /**
     * 显示空頁面
     */
    fun showError(code: Int, message: String?)

    /**
     * 显示Toast提示内容
     */
    fun showToast(message: String)

    /**
     * 显示Toast提示内容
     */
    fun showToast(message: Int)

    /**
     * startActivity 页面跳转
     */
    fun startActivity(cls: Class<*>?)

    /**
     * 退出当前App
     */
    fun killApp()
}