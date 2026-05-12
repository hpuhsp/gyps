@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.app

/**
 * 包路径兼容层 — com.swallow.fly.base.app
 *
 * 原包路径已迁移至：
 *   - AppDelegate   → com.swallow.fly.base.lifecycle.AppDelegate
 *   - AppLifecycles → com.swallow.fly.base.lifecycle.AppLifecycle（接口名去掉了 s）
 *   - AppModule     → com.swallow.fly.base.lifecycle.AppModule
 *   - ConfigModule  → com.swallow.fly.base.lifecycle.ConfigModule
 *
 * 注意：AppComponent 已随 Dagger 手动组件一起移除，Hilt 自动处理，无需替代。
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.lifecycle.AppDelegate instead.",
    replaceWith = ReplaceWith("AppDelegate", "com.swallow.fly.base.lifecycle.AppDelegate")
)
typealias AppDelegate = com.swallow.fly.base.lifecycle.AppDelegate

/**
 * AppLifecycles → AppLifecycle（接口名在新版中去掉了末尾的 s）
 */
@Deprecated(
    message = "Renamed and package moved. Use com.swallow.fly.base.lifecycle.AppLifecycle instead.",
    replaceWith = ReplaceWith("AppLifecycle", "com.swallow.fly.base.lifecycle.AppLifecycle")
)
typealias AppLifecycles = com.swallow.fly.base.lifecycle.AppLifecycle

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.lifecycle.AppModule instead.",
    replaceWith = ReplaceWith("AppModule", "com.swallow.fly.base.lifecycle.AppModule")
)
typealias AppModule = com.swallow.fly.base.lifecycle.AppModule

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.lifecycle.ConfigModule instead.",
    replaceWith = ReplaceWith("ConfigModule", "com.swallow.fly.base.lifecycle.ConfigModule")
)
typealias ConfigModule = com.swallow.fly.base.lifecycle.ConfigModule
