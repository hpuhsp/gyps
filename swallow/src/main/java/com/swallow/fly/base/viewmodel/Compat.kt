@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.viewmodel

/**
 * 包路径兼容层 — com.swallow.fly.base.viewmodel
 *
 * 原包路径已迁移至：
 *   - BaseViewModel → com.swallow.fly.base.presentation.BaseViewModel
 *   - IViewModel    → com.swallow.fly.base.presentation.IViewModel
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.presentation.BaseViewModel instead.",
    replaceWith = ReplaceWith("BaseViewModel", "com.swallow.fly.base.presentation.BaseViewModel")
)
typealias BaseViewModel = com.swallow.fly.base.presentation.BaseViewModel

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.presentation.IViewModel instead.",
    replaceWith = ReplaceWith("IViewModel", "com.swallow.fly.base.presentation.IViewModel")
)
typealias IViewModel = com.swallow.fly.base.presentation.IViewModel
