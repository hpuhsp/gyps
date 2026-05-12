@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.viewstate

/**
 * 包路径兼容层 — com.swallow.fly.base.viewstate
 *
 * 原包路径已迁移至：
 *   - PageListState → com.swallow.fly.base.presentation.state.PageListState
 *   - PageViewState → com.swallow.fly.base.presentation.state.PageViewState
 *   - IViewState    → 已废弃，无对应新类（原为空接口）
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.presentation.state.PageListState instead.",
    replaceWith = ReplaceWith("PageListState", "com.swallow.fly.base.presentation.state.PageListState")
)
typealias PageListState<T> = com.swallow.fly.base.presentation.state.PageListState<T>

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.presentation.state.PageViewState instead.",
    replaceWith = ReplaceWith("PageViewState", "com.swallow.fly.base.presentation.state.PageViewState")
)
typealias PageViewState<T> = com.swallow.fly.base.presentation.state.PageViewState<T>

/**
 * IViewState 原为空接口，已废弃，无需迁移。
 */
@Deprecated(
    message = "IViewState was an empty interface and has been removed. No replacement needed.",
)
interface IViewState
