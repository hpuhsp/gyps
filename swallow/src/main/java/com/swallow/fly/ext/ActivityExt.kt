package com.swallow.fly.ext

import android.app.Activity
import android.os.Build
import androidx.annotation.AnimRes

/**
 * Activity 过渡动画扩展
 * 
 * 兼容新旧 API 的过渡动画方法
 */

/**
 * 设置 Activity 过渡动画（兼容新旧 API）
 * 
 * 在 Android 13 (API 33) 之前使用 overridePendingTransition，
 * 在 Android 13 及以上使用 overrideActivityTransition
 * 
 * 使用示例：
 * ```kotlin
 * finish()
 * overrideTransition(R.anim.fade_in, R.anim.fade_out)
 * ```
 * 
 * @param enterAnim 进入动画资源 ID
 * @param exitAnim 退出动画资源 ID
 */
fun Activity.overrideTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        // Android 14+ (API 34+)
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_CLOSE,
            enterAnim,
            exitAnim
        )
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}

/**
 * 设置 Activity 打开过渡动画（兼容新旧 API）
 * 
 * 用于 startActivity 之后设置动画
 * 
 * @param enterAnim 进入动画资源 ID
 * @param exitAnim 退出动画资源 ID
 */
fun Activity.overrideOpenTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        // Android 14+ (API 34+)
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_OPEN,
            enterAnim,
            exitAnim
        )
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}

/**
 * 完成 Activity 并应用过渡动画
 * 
 * 使用示例：
 * ```kotlin
 * finishWithTransition(R.anim.fade_in, R.anim.fade_out)
 * ```
 * 
 * @param enterAnim 进入动画资源 ID
 * @param exitAnim 退出动画资源 ID
 */
fun Activity.finishWithTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
    finish()
    overrideTransition(enterAnim, exitAnim)
}
