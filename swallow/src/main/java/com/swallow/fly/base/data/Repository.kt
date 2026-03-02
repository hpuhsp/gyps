package com.swallow.fly.base.data

import com.swallow.fly.annotations.Stable

/**
 * Repository 基类集合，支持远程和本地数据源的不同组合
 *
 * 提供四种 Repository 基类，适应不同的数据访问场景：
 * - [BaseRepositoryBoth]: 同时包含远程和本地数据源
 * - [BaseRepositoryLocal]: 仅包含本地数据源
 * - [BaseRepositoryRemote]: 仅包含远程数据源
 * - [BaseRepositoryNothing]: 不包含数据源（直接使用 obtainService）
 *
 * @see BaseRepository
 * @see IRemoteDataSource
 * @see ILocalDataSource
 *
 * @since 1.0.0
 * @author Hsp
 */

/**
 * 同时包含远程和本地数据源的 Repository
 *
 * 使用场景：
 * - 需要从服务器获取数据并缓存到本地数据库的场景
 * - 离线优先（Offline-First）架构
 * - 需要本地缓存以减少网络请求的场景
 *
 * 使用示例：
 * ```kotlin
 * // 1. 定义远程数据源
 * interface UserRemoteDataSource : IRemoteDataSource {
 *     suspend fun getUserInfo(userId: String): User
 * }
 *
 * // 2. 定义本地数据源
 * interface UserLocalDataSource : ILocalDataSource {
 *     suspend fun saveUser(user: User)
 *     suspend fun getUser(userId: String): User?
 * }
 *
 * // 3. 创建 Repository（Hilt 自动注入）
 * @Singleton
 * class UserRepository @Inject constructor(
 *     remoteDataSource: UserRemoteDataSource,
 *     localDataSource: UserLocalDataSource
 * ) : BaseRepositoryBoth<UserRemoteDataSource, UserLocalDataSource>(
 *     remoteDataSource, localDataSource
 * ) {
 *     // 先本地后远程的策略
 *     fun getUserInfo(userId: String): Flow<HttpResult<User>> = flow {
 *         // 先发射本地缓存
 *         localDataSource.getUser(userId)?.let {
 *             emit(HttpResult.Success(it))
 *         }
 *
 *         // 再请求网络数据
 *         val result = executeRequest {
 *             remoteDataSource.getUserInfo(userId)
 *         }
 *
 *         // 成功后更新本地缓存
 *         if (result is HttpResult.Success) {
 *             localDataSource.saveUser(result.value)
 *         }
 *
 *         emit(result)
 *     }.flowOn(Dispatchers.IO)
 * }
 * ```
 *
 * @param R 远程数据源类型
 * @param L 本地数据源类型
 * @param remoteDataSource 远程数据源实例
 * @param localDataSource 本地数据源实例
 *
 * @see BaseRepository
 * @see IRemoteDataSource
 * @see ILocalDataSource
 */
@Stable
open class BaseRepositoryBoth<R : IRemoteDataSource, L : ILocalDataSource>(
    val remoteDataSource: R,
    val localDataSource: L
) : BaseRepository()

/**
 * 仅包含本地数据源的 Repository
 *
 * 使用场景：
 * - 纯离线应用或模块
 * - 只需要操作本地数据库/SharedPreferences/DataStore
 * - 配置管理、用户偏好设置
 *
 * 使用示例：
 * ```kotlin
 * // 1. 定义本地数据源
 * interface SettingsLocalDataSource : ILocalDataSource {
 *     suspend fun saveDarkMode(enabled: Boolean)
 *     suspend fun getDarkMode(): Boolean
 * }
 *
 * // 2. 创建 Repository
 * @Singleton
 * class SettingsRepository @Inject constructor(
 *     localDataSource: SettingsLocalDataSource
 * ) : BaseRepositoryLocal<SettingsLocalDataSource>(localDataSource) {
 *
 *     fun saveDarkModeSetting(enabled: Boolean): Flow<HttpResult<Unit>> {
 *         return flowRequest {
 *             localDataSource.saveDarkMode(enabled)
 *         }
 *     }
 *
 *     fun getDarkModeSetting(): Flow<HttpResult<Boolean>> {
 *         return flowRequest {
 *             localDataSource.getDarkMode()
 *         }
 *     }
 * }
 * ```
 *
 * @param L 本地数据源类型
 * @param localDataSource 本地数据源实例
 *
 * @see BaseRepository
 * @see ILocalDataSource
 */
@Stable
open class BaseRepositoryLocal<L : ILocalDataSource>(
    val localDataSource: L
) : BaseRepository()

/**
 * 仅包含远程数据源的 Repository
 *
 * 使用场景：
 * - 无需缓存的网络请求场景
 * - 实时性要求高的数据（如股票行情、聊天消息）
 * - 一次性操作（如上传文件、提交表单）
 *
 * 使用示例：
 * ```kotlin
 * // 1. 定义远程数据源
 * interface HealthRemoteDataSource : IRemoteDataSource {
 *     suspend fun reportHealthStatus(model: HealthModel): BaseResponse<Any>
 * }
 *
 * // 2. 创建 Repository
 * @Singleton
 * class HealthRepository @Inject constructor(
 *     remoteDataSource: HealthRemoteDataSource
 * ) : BaseRepositoryRemote<HealthRemoteDataSource>(remoteDataSource) {
 *
 *     fun reportHealth(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
 *         return flowRequest {
 *             remoteDataSource.reportHealthStatus(model)
 *         }
 *     }
 * }
 * ```
 *
 * @param R 远程数据源类型
 * @param remoteDataSource 远程数据源实例
 *
 * @see BaseRepository
 * @see IRemoteDataSource
 */
@Stable
open class BaseRepositoryRemote<R : IRemoteDataSource>(
    val remoteDataSource: R
) : BaseRepository()

/**
 * 不包含数据源的 Repository
 *
 * 使用场景：
 * - 直接使用 obtainService 获取 Retrofit Service 的场景
 * - 简单的网络请求，不需要额外抽象数据源层
 * - 快速原型开发
 *
 * 使用示例：
 * ```kotlin
 * // 直接创建 Repository，无需定义 DataSource
 * @Singleton
 * class MainRepository @Inject constructor() : BaseRepositoryNothing() {
 *
 *     fun reportHealthyStatus(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
 *         return flowRequest {
 *             // 直接使用 obtainService 获取 Retrofit Service
 *             obtainService(HealthyService::class.java).reportHealthyStatus(model)
 *         }
 *     }
 * }
 * ```
 *
 * 注意：
 * 这种方式最简单直接，适合小型项目或快速开发。
 * 大型项目建议使用 [BaseRepositoryRemote] 并定义 DataSource 接口，方便测试和解耦。
 *
 * @see BaseRepository
 * @see BaseRepositoryRemote
 */
@Stable
open class BaseRepositoryNothing : BaseRepository()
