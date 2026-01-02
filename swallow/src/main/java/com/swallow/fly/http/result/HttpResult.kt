package com.swallow.fly.http.result

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 10:08
 * @UpdateRemark:   更新说明：
 *   - 2024/12: 添加现代化扩展方法 (map, onSuccess, etc.)
 */
sealed class HttpResult<out T> {
    data class Success<out T>(val value: T) : HttpResult<T>()

    data class Failure(val throwable: Throwable?) : HttpResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }
}

inline fun <T> HttpResult<T>.onSuccess(action: (T) -> Unit): HttpResult<T> {
    if (this is HttpResult.Success) action(value)
    return this
}

inline fun <T> HttpResult<T>.onFailure(action: (Throwable?) -> Unit): HttpResult<T> {
    if (this is HttpResult.Failure) action(throwable)
    return this
}

inline fun <T, R> HttpResult<T>.map(transform: (T) -> R): HttpResult<R> {
    return when (this) {
        is HttpResult.Success -> HttpResult.Success(transform(value))
        is HttpResult.Failure -> HttpResult.Failure(throwable)
    }
}

/**
 * 兼容旧代码的扩展方法
 */
inline fun <reified T> HttpResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is HttpResult.Success) {
        success(value)
    }
}

/**
 * 可调用进行自定义错误处理逻辑
 */
inline fun <reified T> HttpResult<T>.doFailure(
    failure: (Throwable?) -> Unit
) {
    if (this is HttpResult.Failure) {
        failure(throwable)
    }
}