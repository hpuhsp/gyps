package com.swallow.fly.base.repository

/**
 * Repository 基类 - 提供不同数据源组合的基础实现
 * 
 * @Description: Repository 基类，支持远程和本地数据源的不同组合
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/21 16:37
 * @UpdateRemark: 2024 - 现代化升级：实现 IRepository 接口
 */

/**
 * 同时包含远程和本地数据源的 Repository
 */
open class BaseRepositoryBoth<R : IRemoteDataSource, L : ILocalDataSource>(
    val remoteDataSource: R,
    val localDataSource: L
) : BaseRepository()

/**
 * 仅包含本地数据源的 Repository
 */
open class BaseRepositoryLocal<L : ILocalDataSource>(
    val localDataSource: L
) : BaseRepository()

/**
 * 仅包含远程数据源的 Repository
 */
open class BaseRepositoryRemote<R : IRemoteDataSource>(
    val remoteDataSource: R
) : BaseRepository()

/**
 * 不包含数据源的 Repository（用于特殊场景）
 */
open class BaseRepositoryNothing : BaseRepository()