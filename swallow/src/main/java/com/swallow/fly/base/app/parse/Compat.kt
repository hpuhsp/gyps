@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.app.parse

/**
 * 包路径兼容层 — com.swallow.fly.base.app.parse
 *
 * 原包路径已迁移至：
 *   - ManifestParser → com.swallow.fly.base.lifecycle.parse.ManifestParser
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.lifecycle.parse.ManifestParser instead.",
    replaceWith = ReplaceWith(
        "ManifestParser",
        "com.swallow.fly.base.lifecycle.parse.ManifestParser"
    )
)
typealias ManifestParser = com.swallow.fly.base.lifecycle.parse.ManifestParser
