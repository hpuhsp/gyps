package com.swallow.fly.ext.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

/**
 * Navigation 扩展函数
 * 提供便捷的导航操作
 */

/**
 * Fragment 安全导航
 * @param resId 目标 Fragment 的资源 ID
 * @param args 传递的参数
 * @param navOptions 导航选项
 * @param navigatorExtras 导航额外信息
 */
fun Fragment.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        findNavController().navigate(resId, args, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * NavController 安全导航
 */
fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        navigate(resId, args, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 安全返回上一页
 */
fun Fragment.popBackStackSafe(): Boolean {
    return try {
        findNavController().popBackStack()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 安全返回到指定目标
 */
fun Fragment.popBackStackSafe(@IdRes destinationId: Int, inclusive: Boolean = false): Boolean {
    return try {
        findNavController().popBackStack(destinationId, inclusive)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 创建导航选项构建器
 */
fun navOptions(builder: NavOptions.Builder.() -> Unit): NavOptions {
    return NavOptions.Builder().apply(builder).build()
}

/**
 * 带动画的导航选项
 */
fun animNavOptions(
    enterAnim: Int = androidx.navigation.ui.R.anim.nav_default_enter_anim,
    exitAnim: Int = androidx.navigation.ui.R.anim.nav_default_exit_anim,
    popEnterAnim: Int = androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
    popExitAnim: Int = androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(enterAnim)
        .setExitAnim(exitAnim)
        .setPopEnterAnim(popEnterAnim)
        .setPopExitAnim(popExitAnim)
        .build()
}

/**
 * 单栈导航选项（清除栈顶到目标之间的所有页面）
 */
fun singleTopNavOptions(@IdRes popUpTo: Int, inclusive: Boolean = false): NavOptions {
    return NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setPopUpTo(popUpTo, inclusive)
        .build()
}

/**
 * 清空栈并导航
 */
fun clearBackStackNavOptions(@IdRes popUpTo: Int): NavOptions {
    return NavOptions.Builder()
        .setPopUpTo(popUpTo, true)
        .build()
}
