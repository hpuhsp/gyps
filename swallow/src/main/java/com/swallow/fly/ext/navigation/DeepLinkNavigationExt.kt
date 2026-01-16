package com.swallow.fly.ext.navigation

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

/**
 * Deep Link 导航扩展
 * 用于组件化项目的模块间跳转，避免模块依赖耦合
 * 
 * 支持：
 * - Fragment
 * - Activity
 * - View
 * - Dialog
 * - 任何具有 Context 的地方
 * 
 * 使用方式：
 * 1. 在各模块的导航图中定义 Deep Link
 * 2. 各模块维护自己的路由常量（类似 ARouter 的 group）
 * 3. 使用 navigateByDeepLink() 进行跨模块跳转
 * 4. 无需依赖目标模块
 */

// ==================== Fragment 扩展 ====================

/**
 * Fragment 通过 Deep Link 进行导航（字符串 URL）
 * 
 * @param deepLink Deep Link URL，例如 "gyps://user/detail/123"
 * @param navOptions 导航选项
 * @param navigatorExtras 导航额外信息
 * 
 * 示例：
 * ```
 * navigateByDeepLink("gyps://user/detail/123")
 * navigateByDeepLink(UserRoutes.detail(123))
 * ```
 */
fun Fragment.navigateByDeepLink(
    deepLink: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val uri = deepLink.toUri()
        findNavController().navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Fragment 通过 Deep Link 进行导航（Uri）
 */
fun Fragment.navigateByDeepLink(
    uri: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        findNavController().navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==================== Activity 扩展 ====================

/**
 * Activity 通过 Deep Link 进行导航
 * 
 * @param navHostId NavHostFragment 的资源 ID
 * @param deepLink Deep Link URL
 * @param navOptions 导航选项
 * @param navigatorExtras 导航额外信息
 * 
 * 示例：
 * ```
 * navigateByDeepLink(R.id.nav_host_fragment, "gyps://user/detail/123")
 * navigateByDeepLink(R.id.nav_host_fragment, UserRoutes.detail(123))
 * ```
 */
fun Activity.navigateByDeepLink(
    navHostId: Int,
    deepLink: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val navController = Navigation.findNavController(this, navHostId)
        val uri = deepLink.toUri()
        navController.navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Activity 通过 Deep Link 进行导航（Uri）
 */
fun Activity.navigateByDeepLink(
    navHostId: Int,
    uri: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val navController = Navigation.findNavController(this, navHostId)
        navController.navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==================== View 扩展 ====================

/**
 * View 通过 Deep Link 进行导航
 * 
 * @param deepLink Deep Link URL
 * @param navOptions 导航选项
 * @param navigatorExtras 导航额外信息
 * 
 * 示例：
 * ```
 * view.navigateByDeepLink("gyps://user/detail/123")
 * binding.button.navigateByDeepLink(UserRoutes.detail(123))
 * ```
 */
fun View.navigateByDeepLink(
    deepLink: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val navController = Navigation.findNavController(this)
        val uri = deepLink.toUri()
        navController.navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * View 通过 Deep Link 进行导航（Uri）
 */
fun View.navigateByDeepLink(
    uri: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val navController = Navigation.findNavController(this)
        navController.navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==================== Context 扩展（通用） ====================

/**
 * Context 通过 Deep Link 进行导航
 * 需要提供 NavController 或 NavHostFragment 的 View
 * 
 * @param navHostView NavHostFragment 的根 View
 * @param deepLink Deep Link URL
 * @param navOptions 导航选项
 * @param navigatorExtras 导航额外信息
 * 
 * 示例：
 * ```
 * // 在 Dialog 中使用
 * context.navigateByDeepLink(navHostView, "gyps://user/detail/123")
 * 
 * // 在自定义 View 中使用
 * context.navigateByDeepLink(navHostView, UserRoutes.detail(123))
 * ```
 */
fun Context.navigateByDeepLink(
    navHostView: View,
    deepLink: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val navController = Navigation.findNavController(navHostView)
        val uri = deepLink.toUri()
        navController.navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==================== NavController 扩展 ====================

/**
 * NavController 通过 Deep Link 导航
 */
fun NavController.navigateByDeepLink(
    deepLink: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        val uri = deepLink.toUri()
        navigate(uri, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 通过 Deep Link Request 进行导航（支持更复杂的场景）
 * 
 * @param deepLinkRequest Deep Link 请求对象
 * @param navOptions 导航选项
 * @param navigatorExtras 导航额外信息
 */
fun Fragment.navigateByDeepLinkRequest(
    deepLinkRequest: NavDeepLinkRequest,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {
        findNavController().navigate(deepLinkRequest, navOptions, navigatorExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==================== Deep Link 构建器 ====================

/**
 * Deep Link 构建器
 * 提供类型安全的 Deep Link 构建方式
 * 
 * 示例：
 * ```
 * val deepLink = deepLinkBuilder("gyps") {
 *     group("user")  // 路由分组，类似 ARouter 的 group
 *     path("detail")
 *     param("userId", 123)
 *     param("from", "home")
 * }
 * navigateByDeepLink(deepLink)
 * ```
 */
class DeepLinkBuilder(private val scheme: String) {
    private val pathSegments = mutableListOf<String>()
    private val queryParams = mutableMapOf<String, String>()
    
    /**
     * 添加路由分组（类似 ARouter 的 group）
     */
    fun group(group: String) {
        pathSegments.add(group)
    }
    
    /**
     * 添加路径段
     */
    fun path(vararg segments: String) {
        pathSegments.addAll(segments)
    }
    
    /**
     * 添加查询参数
     */
    fun param(key: String, value: Any) {
        queryParams[key] = value.toString()
    }
    
    /**
     * 构建 Deep Link URL
     */
    fun build(): String {
        val path = pathSegments.joinToString("/")
        val query = if (queryParams.isNotEmpty()) {
            "?" + queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
        } else {
            ""
        }
        return "$scheme://$path$query"
    }
}

/**
 * Deep Link 构建器 DSL
 */
fun deepLinkBuilder(scheme: String, builder: DeepLinkBuilder.() -> Unit): String {
    return DeepLinkBuilder(scheme).apply(builder).build()
}

/**
 * 创建 Deep Link Request
 * 
 * 示例：
 * ```
 * val request = createDeepLinkRequest("gyps://user/detail/123") {
 *     setAction("android.intent.action.VIEW")
 *     setMimeType("text/plain")
 * }
 * navigateByDeepLinkRequest(request)
 * ```
 */
fun createDeepLinkRequest(
    uri: String,
    builder: NavDeepLinkRequest.Builder.() -> Unit = {}
): NavDeepLinkRequest {
    return NavDeepLinkRequest.Builder
        .fromUri(uri.toUri())
        .apply(builder)
        .build()
}

/**
 * 路由常量管理建议
 * 
 * 各模块应该在自己的模块内定义路由常量，而不是集中管理
 * 
 * 示例：
 * ```
 * // user_module/src/main/java/com/example/user/UserRoutes.kt
 * object UserRoutes {
 *     private const val SCHEME = "gyps"
 *     private const val GROUP = "user"  // 路由分组，类似 ARouter 的 group
 *     
 *     fun detail(userId: Long): String = "$SCHEME://$GROUP/detail/$userId"
 *     fun profile(userId: Long): String = "$SCHEME://$GROUP/profile/$userId"
 *     fun edit(userId: Long): String = "$SCHEME://$GROUP/edit/$userId"
 * }
 * 
 * // order_module/src/main/java/com/example/order/OrderRoutes.kt
 * object OrderRoutes {
 *     private const val SCHEME = "gyps"
 *     private const val GROUP = "order"
 *     
 *     fun detail(orderId: String): String = "$SCHEME://$GROUP/detail/$orderId"
 *     fun list(status: String = "all"): String = "$SCHEME://$GROUP/list?status=$status"
 * }
 * ```
 * 
 * 使用时：
 * ```
 * // Fragment 中
 * navigateByDeepLink(UserRoutes.detail(123))
 * 
 * // Activity 中
 * navigateByDeepLink(R.id.nav_host_fragment, UserRoutes.detail(123))
 * 
 * // View 中
 * button.navigateByDeepLink(UserRoutes.detail(123))
 * 
 * // Dialog 中
 * context.navigateByDeepLink(navHostView, UserRoutes.detail(123))
 * ```
 */
