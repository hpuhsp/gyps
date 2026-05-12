@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base

/**
 * 包路径兼容层 — com.swallow.fly.base（根包）
 *
 * 原根包中的类已迁移至：
 *   - BaseApplication → com.swallow.fly.base.lifecycle.BaseApplication
 *   - IActivity       → com.swallow.fly.base.ui.activity.IActivity
 *   - IFragment       → com.swallow.fly.base.ui.fragment.IFragment
 *   - ViewBehavior    → com.swallow.fly.base.ui.ViewBehavior
 *   - PageStateEvent  → com.swallow.fly.base.presentation.state.PageStateEvent（新增对应类）
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.lifecycle.BaseApplication instead.",
    replaceWith = ReplaceWith("BaseApplication", "com.swallow.fly.base.lifecycle.BaseApplication")
)
typealias BaseApplication = com.swallow.fly.base.lifecycle.BaseApplication

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.ui.activity.IActivity instead.",
    replaceWith = ReplaceWith("IActivity", "com.swallow.fly.base.ui.activity.IActivity")
)
typealias IActivity = com.swallow.fly.base.ui.activity.IActivity

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.ui.fragment.IFragment instead.",
    replaceWith = ReplaceWith("IFragment", "com.swallow.fly.base.ui.fragment.IFragment")
)
typealias IFragment = com.swallow.fly.base.ui.fragment.IFragment

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.ui.ViewBehavior instead.",
    replaceWith = ReplaceWith("ViewBehavior", "com.swallow.fly.base.ui.ViewBehavior")
)
typealias ViewBehavior = com.swallow.fly.base.ui.ViewBehavior

@Deprecated(
    message = "PageStateEvent has been replaced by UiState/UiEvent. " +
            "See com.swallow.fly.base.presentation.state.UiState and UiEvent.",
    replaceWith = ReplaceWith("UiState", "com.swallow.fly.base.presentation.state.UiState")
)
data class PageStateEvent(
    val showLoading: Boolean = false,
    val loadingMsg: String? = null,
    val showEmpty: Boolean = false,
    val showError: Boolean = false,
    val errorCode: Int = -1,
    val errorMsg: String? = null
)
