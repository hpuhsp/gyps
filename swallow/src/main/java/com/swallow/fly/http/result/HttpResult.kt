package com.swallow.fly.http.result

import com.swallow.fly.annotations.Stable

/**
 * HTTP 请求结果的封装类型
 *
 * 使用密封类（sealed class）表示请求的两种可能结果：
 * - [Success]: 请求成功，包含返回数据
 * - [Failure]: 请求失败，包含异常信息
 *
 * 核心特性：
 * - **类型安全**：编译时保证处理所有可能的结果
 * - **函数式 API**：提供 [onSuccess]、[onFailure]、[map] 等扩展方法
 * - **空安全**：通过 [getOrNull] 安全获取数据
 *
 * 使用示例：
 * ```kotlin
 * // 在 Repository 中返回
 * fun getUserInfo(userId: String): Flow<HttpResult<User>> {
 *     return flowRequest {
 *         obtainService(UserService::class.java).getUserInfo(userId)
 *     }
 * }
 *
 * // 在 ViewModel 中处理
 * repository.getUserInfo(userId).collect { result ->
 *     result.onSuccess { user ->
 *         _uiState.value = UiState.Success(user)
 *     }.onFailure { error ->
 *         _uiState.value = UiState.Error(error?.message ?: "Unknown error")
 *     }
 * }
 *
 * // 使用 when 表达式
 * when (result) {
 *     is HttpResult.Success -> println("Data: ${result.value}")
 *     is HttpResult.Failure -> println("Error: ${result.throwable}")
 * }
 *
 * // 数据转换
 * val nameResult = userResult.map { it.name }
 * ```
 *
 * @param T 成功时返回的数据类型
 *
 * @see onSuccess
 * @see onFailure
 * @see map
 *
 * @since 1.0.0
 * @author Hsp
 */
@Stable
sealed class HttpResult<out T> {
    /**
     * 请求成功
     *
     * @param value 返回的数据
     */
    data class Success<out T>(val value: T) : HttpResult<T>()

    /**
     * 请求失败
     *
     * @param throwable 异常信息
     */
    data class Failure(val throwable: Throwable?) : HttpResult<Nothing>()

    /**
     * 是否成功
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * 是否失败
     */
    val isFailure: Boolean get() = this is Failure

    /**
     * 安全获取数据，失败时返回 null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }
}

/**
 * 成功时执行操作
 *
 * 如果结果是 [HttpResult.Success]，则执行 [action]，并返回原结果以支持链式调用
 *
 * @param action 成功时执行的操作
 * @return 原结果
 */
inline fun <T> HttpResult<T>.onSuccess(action: (T) -> Unit): HttpResult<T> {
    if (this is HttpResult.Success) action(value)
    return this
}

/**
 * 失败时执行操作
 *
 * 如果结果是 [HttpResult.Failure]，则执行 [action]，并返回原结果以支持链式调用
 *
 * @param action 失败时执行的操作
 * @return 原结果
 */
inline fun <T> HttpResult<T>.onFailure(action: (Throwable?) -> Unit): HttpResult<T> {
    if (this is HttpResult.Failure) action(throwable)
    return this
}

/**
 * 转换成功结果的数据
 *
 * 如果结果是 [HttpResult.Success]，则对数据应用 [transform] 转换
 * 如果结果是 [HttpResult.Failure]，则直接返回失败结果
 *
 * @param transform 数据转换函数
 * @return 转换后的结果
 */
inline fun <T, R> HttpResult<T>.map(transform: (T) -> R): HttpResult<R> {
    return when (this) {
        is HttpResult.Success -> HttpResult.Success(transform(value))
        is HttpResult.Failure -> HttpResult.Failure(throwable)
    }
}

/**
 * 成功时执行操作（兼容旧代码）
 *
 * @deprecated 使用 [onSuccess] 替代
 */
@Deprecated(
    message = "Use onSuccess instead",
    replaceWith = ReplaceWith("onSuccess(success)"),
    level = DeprecationLevel.WARNING
)
inline fun <reified T> HttpResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is HttpResult.Success) {
        success(value)
    }
}

/**
 * 失败时执行操作（兼容旧代码）
 *
 * @deprecated 使用 [onFailure] 替代
 */
@Deprecated(
    message = "Use onFailure instead",
    replaceWith = ReplaceWith("onFailure(failure)"),
    level = DeprecationLevel.WARNING
)
inline fun <reified T> HttpResult<T>.doFailure(
    failure: (Throwable?) -> Unit
) {
    if (this is HttpResult.Failure) {
        failure(throwable)
    }
}