@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.app.config

/**
 * 包路径兼容层 — com.swallow.fly.base.app.config
 *
 * 原包路径已迁移至：
 *   - GlobalConfiguration → com.swallow.fly.base.lifecycle.config.GlobalConfiguration
 *   - GlobalConfigModule  → 已移除（Hilt 自动处理，无需手动 Dagger Module）
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.lifecycle.config.GlobalConfiguration instead.",
    replaceWith = ReplaceWith(
        "GlobalConfiguration",
        "com.swallow.fly.base.lifecycle.config.GlobalConfiguration"
    )
)
typealias GlobalConfiguration = com.swallow.fly.base.lifecycle.config.GlobalConfiguration
