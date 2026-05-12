package com.swallow.fly.ext

import android.content.Context
import android.widget.Toast

/**
 * Toast 扩展函数
 *
 * 提供更简洁的 Toast 使用方式，替代 FastUtils.makeText()
 *
 * @author Kiro
 * @since 2026-03-13
 */

private var toast: Toast? = null

/**
 * 显示短时 Toast
 *
 * 使用单例模式，避免重复显示
 *
 * @param message 要显示的消息
 *
 * 示例：
 * ```kotlin
 * context.showToast("登录成功")
 * ```
 */
fun Context.showToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply { show() }
}

/**
 * 显示短时 Toast（支持资源 ID）
 *
 * @param resId 字符串资源 ID
 *
 * 示例：
 * ```kotlin
 * context.showToast(R.string.login_success)
 * ```
 */
fun Context.showToast(resId: Int) {
    showToast(getString(resId))
}

/**
 * 显示长时 Toast
 *
 * @param message 要显示的消息
 *
 * 示例：
 * ```kotlin
 * context.showLongToast("这是一条较长的提示信息")
 * ```
 */
fun Context.showLongToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply { show() }
}

/**
 * 显示长时 Toast（支持资源 ID）
 *
 * @param resId 字符串资源 ID
 */
fun Context.showLongToast(resId: Int) {
    showLongToast(getString(resId))
}

/**
 * 取消当前显示的 Toast
 */
fun cancelToast() {
    toast?.cancel()
    toast = null
}
