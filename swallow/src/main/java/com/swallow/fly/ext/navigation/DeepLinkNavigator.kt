package com.swallow.fly.ext.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.Fragment

/**
 * Deep Link 导航器
 * 
 * 简化的组件化导航方案，无需配置导航图
 * 直接使用 Intent + Deep Link 实现模块间跳转
 * 
 * 优势：
 * - 无需配置导航图
 * - 无需主模块引入子模块导航图
 * - 完全解耦，类似 ARouter
 * - 支持所有上下文（Activity、Fragment、View、Dialog等）
 * 
 * 使用方式：
 * 1. 各模块定义自己的路由常量
 * 2. 在 AndroidManifest.xml 中声明 intent-filter
 * 3. 使用 navigateByDeepLink() 跳转
 */
object DeepLinkNavigator {
    
    /**
     * 通过 Deep Link 启动 Activity
     * 
     * @param context 上下文
     * @param deepLink Deep Link URL
     * @param extras 额外参数
     * @param flags Intent flags
     * 
     * 示例：
     * ```
     * DeepLinkNavigator.navigate(context, "gyps://user/detail/123")
     * DeepLinkNavigator.navigate(context, UserRoutes.detail(123))
     * ```
     */
    fun navigate(
        context: Context,
        deepLink: String,
        extras: Bundle? = null,
        flags: Int? = null
    ) {
        try {
            val uri = deepLink.toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                extras?.let { putExtras(it) }
                flags?.let { addFlags(it) }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 通过 Deep Link 启动 Activity（带动画）
     * 
     * @param context 上下文
     * @param deepLink Deep Link URL
     * @param extras 额外参数
     * @param enterAnim 进入动画
     * @param exitAnim 退出动画
     */
    fun navigate(
        context: Context,
        deepLink: String,
        extras: Bundle? = null,
        enterAnim: Int = 0,
        exitAnim: Int = 0
    ) {
        try {
            val uri = deepLink.toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                extras?.let { putExtras(it) }
            }
            context.startActivity(intent)
            
            // 应用动画
            if (context is Activity && enterAnim != 0 && exitAnim != 0) {
                context.overridePendingTransition(enterAnim, exitAnim)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 通过 Deep Link 启动 Activity 并期待返回结果
     * 
     * @param activity Activity 上下文
     * @param deepLink Deep Link URL
     * @param requestCode 请求码
     * @param extras 额外参数
     */
    fun navigateForResult(
        activity: Activity,
        deepLink: String,
        requestCode: Int,
        extras: Bundle? = null
    ) {
        try {
            val uri = deepLink.toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                extras?.let { putExtras(it) }
            }
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 检查是否可以处理该 Deep Link
     * 
     * @param context 上下文
     * @param deepLink Deep Link URL
     * @return true 表示可以处理，false 表示无法处理
     */
    fun canHandle(context: Context, deepLink: String): Boolean {
        return try {
            val uri = deepLink.toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val packageManager = context.packageManager
            intent.resolveActivity(packageManager) != null
        } catch (e: Exception) {
            false
        }
    }
}

// ==================== Context 扩展 ====================

/**
 * Context 通过 Deep Link 导航
 * 
 * 示例：
 * ```
 * context.navigateByDeepLink("gyps://user/detail/123")
 * context.navigateByDeepLink(UserRoutes.detail(123))
 * ```
 */
fun Context.navigateByDeepLink(
    deepLink: String,
    extras: Bundle? = null,
    flags: Int? = null
) {
    DeepLinkNavigator.navigate(this, deepLink, extras, flags)
}

/**
 * Context 通过 Deep Link 导航（带动画）
 */
fun Context.navigateByDeepLink(
    deepLink: String,
    extras: Bundle? = null,
    enterAnim: Int = 0,
    exitAnim: Int = 0
) {
    DeepLinkNavigator.navigate(this, deepLink, extras, enterAnim, exitAnim)
}

/**
 * 检查是否可以处理该 Deep Link
 */
fun Context.canHandleDeepLink(deepLink: String): Boolean {
    return DeepLinkNavigator.canHandle(this, deepLink)
}

// ==================== Fragment 扩展 ====================

/**
 * Fragment 通过 Deep Link 导航
 * 
 * 示例：
 * ```
 * navigateByDeepLink("gyps://user/detail/123")
 * navigateByDeepLink(UserRoutes.detail(123))
 * ```
 */
fun Fragment.navigateByDeepLink(
    deepLink: String,
    extras: Bundle? = null,
    flags: Int? = null
) {
    DeepLinkNavigator.navigate(requireContext(), deepLink, extras, flags)
}

/**
 * Fragment 通过 Deep Link 导航（带动画）
 */
fun Fragment.navigateByDeepLink(
    deepLink: String,
    extras: Bundle? = null,
    enterAnim: Int = 0,
    exitAnim: Int = 0
) {
    DeepLinkNavigator.navigate(requireContext(), deepLink, extras, enterAnim, exitAnim)
}

// ==================== Activity 扩展 ====================

/**
 * Activity 通过 Deep Link 导航并期待返回结果
 * 
 * 示例：
 * ```
 * navigateByDeepLinkForResult("gyps://user/select", REQUEST_CODE_SELECT_USER)
 * ```
 */
fun Activity.navigateByDeepLinkForResult(
    deepLink: String,
    requestCode: Int,
    extras: Bundle? = null
) {
    DeepLinkNavigator.navigateForResult(this, deepLink, requestCode, extras)
}

/**
 * 路由常量定义示例
 * 
 * 各模块应该在自己的模块内定义路由常量：
 * 
 * ```
 * // user_module/UserRoutes.kt
 * object UserRoutes {
 *     private const val SCHEME = "gyps"
 *     private const val GROUP = "user"  // 路由分组，类似 ARouter 的 group
 *     
 *     fun detail(userId: Long): String = "$SCHEME://$GROUP/detail/$userId"
 *     fun profile(userId: Long): String = "$SCHEME://$GROUP/profile/$userId"
 *     fun edit(userId: Long): String = "$SCHEME://$GROUP/edit/$userId"
 *     fun list(type: String = "all"): String = "$SCHEME://$GROUP/list?type=$type"
 * }
 * 
 * // order_module/OrderRoutes.kt
 * object OrderRoutes {
 *     private const val SCHEME = "gyps"
 *     private const val GROUP = "order"
 *     
 *     fun detail(orderId: String): String = "$SCHEME://$GROUP/detail/$orderId"
 *     fun list(status: String = "all"): String = "$SCHEME://$GROUP/list?status=$status"
 *     fun create(): String = "$SCHEME://$GROUP/create"
 * }
 * ```
 * 
 * AndroidManifest.xml 配置：
 * 
 * ```xml
 * <!-- user_module/AndroidManifest.xml -->
 * <activity
 *     android:name=".UserDetailActivity"
 *     android:exported="true">
 *     <intent-filter>
 *         <action android:name="android.intent.action.VIEW" />
 *         <category android:name="android.intent.category.DEFAULT" />
 *         <category android:name="android.intent.category.BROWSABLE" />
 *         <data
 *             android:scheme="gyps"
 *             android:host="user"
 *             android:pathPrefix="/detail" />
 *     </intent-filter>
 * </activity>
 * ```
 * 
 * 使用示例：
 * 
 * ```
 * // 在任何地方跳转
 * navigateByDeepLink(UserRoutes.detail(123))
 * navigateByDeepLink(OrderRoutes.list("pending"))
 * 
 * // 带额外参数
 * val extras = bundleOf("from" to "home")
 * navigateByDeepLink(UserRoutes.detail(123), extras)
 * 
 * // 带动画
 * navigateByDeepLink(
 *     UserRoutes.detail(123),
 *     enterAnim = R.anim.slide_in_right,
 *     exitAnim = R.anim.slide_out_left
 * )
 * 
 * // 检查是否可以处理
 * if (canHandleDeepLink(UserRoutes.detail(123))) {
 *     navigateByDeepLink(UserRoutes.detail(123))
 * }
 * ```
 * 
 * 在目标 Activity 中接收参数：
 * 
 * ```
 * class UserDetailActivity : BaseActivity<...>() {
 *     private val userId: Long by lazy {
 *         // 从 URI 路径中获取
 *         intent.data?.lastPathSegment?.toLongOrNull() ?: 0L
 *     }
 *     
 *     private val from: String? by lazy {
 *         // 从 extras 中获取
 *         intent.getStringExtra("from")
 *     }
 *     
 *     override fun initView() {
 *         viewModel.loadUserDetail(userId)
 *     }
 * }
 * ```
 */
