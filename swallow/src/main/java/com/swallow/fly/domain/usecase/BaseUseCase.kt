package com.swallow.fly.domain.usecase

import android.util.Log
import timber.log.Timber

/**
 * UseCase 抽象基类
 *
 * 提供通用的错误处理逻辑和日志记录功能。
 *
 * @param P 输入参数类型
 * @param R 返回结果类型
 *
 * @author Swallow Framework
 * @since 2.0.0
 *
 * 使用示例：
 * ```kotlin
 * class GetUserProfileUseCase @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : BaseUseCase<String, UserProfile>() {
 *     override suspend fun execute(params: String): UserProfile {
 *         return userRepository.getUserProfile(params).getOrThrow()
 *     }
 * }
 * ```
 */
abstract class BaseUseCase<in P, out R> : UseCase<P, R> {

    /**
     * 执行 UseCase
     *
     * 自动处理异常并转换为 AppException
     *
     * @param params 输入参数
     * @return Result<R> 执行结果
     */
    override suspend fun invoke(params: P): Result<R> {
        return try {
            Timber.tag(TAG).d("Executing ${this::class.simpleName} with params: $params")
            val result = execute(params)
            Timber.tag(TAG).d("Successfully executed ${this::class.simpleName}")
            Result.success(result)
        } catch (e: Exception) {
            Timber.tag(TAG).e("Failed to execute ${this::class.simpleName}: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 执行具体的业务逻辑
     *
     * 子类需要实现此方法，专注于业务逻辑，无需处理异常。
     *
     * @param params 输入参数
     * @return R 执行结果
     * @throws Exception 任何异常都会被自动捕获并转换为 AppException
     */
    protected abstract suspend fun execute(params: P): R

    companion object {
        private const val TAG = "BaseUseCase"
    }
}
