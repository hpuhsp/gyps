package com.swallow.fly.http.ext

import com.swallow.fly.http.exception.ApiException
import com.swallow.fly.http.result.BaseResponse
import com.swallow.fly.http.result.HttpResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * BaseResponse 扩展函数 - 自动解包
 * 
 * 提供便捷的 API 自动处理 BaseResponse 的业务逻辑:
 * - 自动检查 isSuccessful()
 * - 成功时提取 data
 * - 失败时转换为 ApiException
 * 
 * 使用场景:
 * ```kotlin
 * // Repository 中
 * fun getUserInfo(userId: String): Flow<HttpResult<User>> {
 *     return flowRequest {
 *         obtainService(UserService::class.java).getUserInfo(userId)
 *     }.unwrap()  // 自动解包
 * }
 * 
 * // ViewModel 中
 * repository.getUserInfo(userId).collect { result ->
 *     result.onSuccess { user ->  // 直接得到 User 对象
 *         _uiState.value = UiState.Success(user)
 *     }
 * }
 * ```
 * 
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 2.0.0
 */

/**
 * 将 HttpResult<BaseResponse<T>> 转换为 HttpResult<T>
 * 
 * 自动处理业务错误码:
 * - 如果 response.isSuccessful() 返回 true,提取 data
 * - 如果返回 false,转换为 ApiException
 * 
 * @return 解包后的 HttpResult<T>
 */
fun <T> HttpResult<BaseResponse<T>>.unwrap(): HttpResult<T> {
    return when (this) {
        is HttpResult.Success -> {
            if (value.isSuccessful()) {
                HttpResult.Success(value.data)
            } else {
                HttpResult.Failure(
                    ApiException(
                        code = value.code,
                        message = value.message
                    )
                )
            }
        }
        is HttpResult.Failure -> HttpResult.Failure(throwable)
    }
}

/**
 * Flow 版本的自动解包
 * 
 * 将 Flow<HttpResult<BaseResponse<T>>> 转换为 Flow<HttpResult<T>>
 * 
 * @return 解包后的 Flow
 */
fun <T> Flow<HttpResult<BaseResponse<T>>>.unwrap(): Flow<HttpResult<T>> {
    return map { it.unwrap() }
}

/**
 * 安全解包 - 提供默认值
 * 
 * 如果解包失败,返回默认值而不是异常
 * 
 * @param defaultValue 默认值
 * @return 解包后的结果或默认值
 */
fun <T> HttpResult<BaseResponse<T>>.unwrapOrDefault(defaultValue: T): T {
    return when (val result = this.unwrap()) {
        is HttpResult.Success -> result.value
        is HttpResult.Failure -> defaultValue
    }
}

/**
 * 安全解包 - 提供默认值生成器
 * 
 * 如果解包失败,使用 defaultValue 函数生成默认值
 * 
 * @param defaultValue 默认值生成器
 * @return 解包后的结果或默认值
 */
inline fun <T> HttpResult<BaseResponse<T>>.unwrapOrElse(defaultValue: (Throwable?) -> T): T {
    return when (val result = this.unwrap()) {
        is HttpResult.Success -> result.value
        is HttpResult.Failure -> defaultValue(result.throwable)
    }
}
