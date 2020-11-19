package com.swallow.fly.base.repository

/**
 * @Description:
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/8/21 16:37
 * @UpdateRemark:
 */
open class BaseRepositoryBoth<R : IRemoteDataSource, L : ILocalDataSource>(
    val remoteDataSource: R,
    val localDataSource: L
) : IRepository

open class BaseRepositoryLocal<L : ILocalDataSource>(
    val localDataSource: L
) : IRepository

open class BaseRepositoryRemote<R : IRemoteDataSource>(
    val remoteDataSource: R
) : IRepository

open class BaseRepositoryNothing() : IRepository