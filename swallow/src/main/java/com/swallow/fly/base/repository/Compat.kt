@file:Suppress("DEPRECATION", "unused")
package com.swallow.fly.base.repository

/**
 * 包路径兼容层 — com.swallow.fly.base.repository
 *
 * 原包路径已迁移至：
 *   - BaseRepository → com.swallow.fly.base.data.BaseRepository
 *   - IRepository    → com.swallow.fly.base.data.IRepository
 *   - Repository     → com.swallow.fly.base.data.Repository（含 BaseRepositoryBoth 等）
 *
 * 此文件仅用于向后兼容，将在 3.0.0 版本移除。
 */

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.data.BaseRepository instead.",
    replaceWith = ReplaceWith("BaseRepository", "com.swallow.fly.base.data.BaseRepository")
)
typealias BaseRepository = com.swallow.fly.base.data.BaseRepository

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.data.IRepository instead.",
    replaceWith = ReplaceWith("IRepository", "com.swallow.fly.base.data.IRepository")
)
typealias IRepository = com.swallow.fly.base.data.IRepository

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.data.IRemoteDataSource instead.",
    replaceWith = ReplaceWith("IRemoteDataSource", "com.swallow.fly.base.data.IRemoteDataSource")
)
typealias IRemoteDataSource = com.swallow.fly.base.data.IRemoteDataSource

@Deprecated(
    message = "Package moved. Use com.swallow.fly.base.data.ILocalDataSource instead.",
    replaceWith = ReplaceWith("ILocalDataSource", "com.swallow.fly.base.data.ILocalDataSource")
)
typealias ILocalDataSource = com.swallow.fly.base.data.ILocalDataSource
