package com.swallow.fly.ext

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.WindowManager

/**
 * Activity 相关扩展函数
 *
 * 替代 FastUtils 中的 Activity 操作方法
 */

/** 跳转到指定 Activity */
fun Activity.startActivity(clazz: Class<*>) {
    startActivity(Intent(applicationContext, clazz))
}

/**
 * 全屏沉浸式状态栏
 *
 * API 30+ 使用 setDecorFitsSystemWindows，旧版本使用 FLAG_LAYOUT_NO_LIMITS
 */
@Suppress("DEPRECATION")
fun Activity.enterImmersiveFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
    } else {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}

/**
 * 带过渡动画关闭当前 Activity
 *
 * API 34+ 使用 overrideActivityTransition，旧版本使用 overridePendingTransition
 *
 * @param enterAnim 进入动画资源 ID
 * @param exitAnim  退出动画资源 ID
 */
@Suppress("DEPRECATION")
fun Activity.finishWithTransition(enterAnim: Int, exitAnim: Int) {
    finish()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, enterAnim, exitAnim)
    } else {
        overridePendingTransition(enterAnim, exitAnim)
    }
}

/**
 * 带过渡动画启动 Activity（覆盖打开动画）
 *
 * API 34+ 使用 overrideActivityTransition，旧版本使用 overridePendingTransition
 *
 * @param enterAnim 进入动画资源 ID
 * @param exitAnim  退出动画资源 ID
 */
@Suppress("DEPRECATION")
fun Activity.overrideOpenTransition(enterAnim: Int, exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
    } else {
        overridePendingTransition(enterAnim, exitAnim)
    }
}
