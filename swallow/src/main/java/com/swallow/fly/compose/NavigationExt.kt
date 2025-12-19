package com.swallow.fly.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * Navigation Compose 辅助函数
 */

/**
 * 导航事件处理
 * 用于处理 ViewModel 中的导航事件
 *
 * @param navigationEvents 导航事件流
 * @param navController 导航控制器
 */
@Composable
fun HandleNavigationEvents(
    navigationEvents: Flow<NavigationEvent>,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        event.popUpTo?.let { route ->
                            popUpTo(route) {
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is NavigationEvent.NavigateBackTo -> {
                    navController.popBackStack(event.route, event.inclusive)
                }
            }
        }
    }
}

/**
 * 导航事件密封类
 */
sealed class NavigationEvent {
    /**
     * 导航到指定路由
     *
     * @param route 目标路由
     * @param popUpTo 弹出到指定路由
     * @param inclusive 是否包含 popUpTo 的路由
     * @param singleTop 是否使用 singleTop 模式
     */
    data class NavigateTo(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = true
    ) : NavigationEvent()

    /**
     * 返回上一页
     */
    object NavigateBack : NavigationEvent()

    /**
     * 返回到指定路由
     *
     * @param route 目标路由
     * @param inclusive 是否包含目标路由
     */
    data class NavigateBackTo(
        val route: String,
        val inclusive: Boolean = false
    ) : NavigationEvent()
}

/**
 * 安全导航扩展函数
 * 避免重复导航导致的崩溃
 */
fun NavController.navigateSafe(
    route: String,
    popUpTo: String? = null,
    inclusive: Boolean = false,
    singleTop: Boolean = true
) {
    try {
        navigate(route) {
            popUpTo?.let { route ->
                popUpTo(route) {
                    this.inclusive = inclusive
                }
            }
            launchSingleTop = singleTop
        }
    } catch (e: Exception) {
        // 忽略导航异常
        timber.log.Timber.e(e, "Navigation error")
    }
}

/**
 * 安全返回扩展函数
 */
fun NavController.popBackStackSafe(): Boolean {
    return try {
        popBackStack()
    } catch (e: Exception) {
        timber.log.Timber.e(e, "PopBackStack error")
        false
    }
}
