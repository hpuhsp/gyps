@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.event

/**
 * 包路径兼容层 — com.swallow.fly.base.event
 *
 * 原包路径已迁移至：
 *   - BaseStateEvent → com.swallow.fly.base.presentation.state.BaseStateEvent
 *   - EventArgs      → com.swallow.fly.base.presentation.state.EventArgs
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.presentation.state.BaseStateEvent instead.",
    replaceWith = ReplaceWith("BaseStateEvent", "com.swallow.fly.base.presentation.state.BaseStateEvent")
)
typealias BaseStateEvent = com.swallow.fly.base.presentation.state.BaseStateEvent

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.presentation.state.EventArgs instead.",
    replaceWith = ReplaceWith("EventArgs", "com.swallow.fly.base.presentation.state.EventArgs")
)
typealias EventArgs = com.swallow.fly.base.presentation.state.EventArgs
