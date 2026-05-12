@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.view

import androidx.viewbinding.ViewBinding
import com.swallow.fly.base.presentation.BaseViewModel

/**
 * 包路径兼容层 — com.swallow.fly.base.view
 *
 * 原包路径已迁移至：
 *   - BaseActivity     → com.swallow.fly.base.ui.activity.BaseActivity
 *   - BaseFragment     → com.swallow.fly.base.ui.fragment.BaseFragment
 *   - BaseLazyFragment → com.swallow.fly.base.ui.fragment.BaseLazyFragment
 *   - FastBaseActivity → com.swallow.fly.base.ui.activity.BaseActivity（已合并，需补充 VB 泛型）
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.ui.activity.BaseActivity instead.",
    replaceWith = ReplaceWith("BaseActivity", "com.swallow.fly.base.ui.activity.BaseActivity")
)
typealias BaseActivity<VM, VB> = com.swallow.fly.base.ui.activity.BaseActivity<VM, VB>

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.ui.fragment.BaseFragment instead.",
    replaceWith = ReplaceWith("BaseFragment", "com.swallow.fly.base.ui.fragment.BaseFragment")
)
typealias BaseFragment<VM, VB> = com.swallow.fly.base.ui.fragment.BaseFragment<VM, VB>

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.ui.fragment.BaseLazyFragment instead.",
    replaceWith = ReplaceWith("BaseLazyFragment", "com.swallow.fly.base.ui.fragment.BaseLazyFragment")
)
typealias BaseLazyFragment<VM, VB> = com.swallow.fly.base.ui.fragment.BaseLazyFragment<VM, VB>

/**
 * FastBaseActivity 兼容包装类
 *
 * 原 FastBaseActivity 不使用 ViewBinding，新版已合并入 BaseActivity（需要 VB 泛型参数）。
 *
 * 迁移方式：
 * ```kotlin
 * // 旧写法
 * class MyActivity : FastBaseActivity<MyViewModel>()
 *
 * // 新写法
 * class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
 *     override val bindingInflater = ActivityMyBinding::inflate
 * }
 * ```
 */
@Deprecated(
    message = "FastBaseActivity has been merged into BaseActivity. " +
            "Migrate to com.swallow.fly.base.ui.activity.BaseActivity with a ViewBinding type parameter.",
    replaceWith = ReplaceWith("BaseActivity", "com.swallow.fly.base.ui.activity.BaseActivity")
)
abstract class FastBaseActivity<VM : BaseViewModel, VB : ViewBinding> :
    com.swallow.fly.base.ui.activity.BaseActivity<VM, VB>()
