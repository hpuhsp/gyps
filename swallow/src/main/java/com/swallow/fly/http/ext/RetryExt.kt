package com.swallow.fly.http.ext

import com.swallow.fly.http.exception.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.pow

/**
 * Flow 重试扩展 - 智能重试策略
 * 
 * 提供指数退避算法的智能重试机制:
 * - 根据异常类型判断是否应该重试
 * - 使用指数退避算法避免服务器压力
 * - 支持自定义重试条件
 * 
 * 使用场景:
 * ```kotlin
 * // Repository 中
 * fun getUserInfo(userId: String): Flow<HttpResult<User>> {
 *     return flowRequest {
 *         obtainService(UserService::class.java).getUserInfo(userId)
 *     }
 *     .unwrap()
 *     .retryWithExponentialBackoff(maxRetries = 3)  // 自动重试
 * }
 * ```
 * 
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 2.0.0
 */

/**
 * 智能重试 - 使用指数退避算法
 * 
 * 重试策略:
 * - 第 1 次重试: 延迟 initialDelay (默认 1 秒)
 * - 第 2 次重试: 延迟 initialDelay * factor (默认 2 秒)
 * - 第 3 次重试: 延迟 initialDelay * factor^2 (默认 4 秒)
 * - 最大延迟不超过 maxDelay (默认 10 秒)
 * 
 * @param maxRetries 最大重试次数 (默认 3 次)
 * @param initialDelay 初始延迟时间(毫秒,默认 1000ms)
 * @param maxDelay 最大延迟时间(毫秒,默认 10000ms)
 * @param factor 延迟倍数(默认 2.0,即每次延迟翻倍)
 * @param shouldRetry 判断是否应该重试的函数
 * @return 带重试逻辑的 Flow
 */
fun <T> Flow<T>.retryWithExponentialBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 1000L,
    maxDelay: Long = 10000L,
    factor: Double = 2.0,
    shouldRetry: (Throwable) -> Boolean = { it.isRetryable() }
): Flow<T> = retryWhen { cause, attempt ->
    // 检查是否应该重试
    if (attempt >= maxRetries || !shouldRetry(cause)) {
        false  // 不重试
    } else {
        // 计算延迟时间(指数退避)
        val delayTime = (initialDelay * factor.pow(attempt.toInt()))
            .toLong()
            .coerceAtMost(maxDelay)
        
        // 延迟后重试
        delay(delayTime)
        true  // 重试
    }
}

/**
 * 简单重试 - 固定延迟
 * 
 * @param maxRetries 最大重试次数
 * @param delayMillis 每次重试的延迟时间(毫秒)
 * @param shouldRetry 判断是否应该重试的函数
 * @return 带重试逻辑的 Flow
 */
fun <T> Flow<T>.retryWithDelay(
    maxRetries: Int = 3,
    delayMillis: Long = 1000L,
    shouldRetry: (Throwable) -> Boolean = { it.isRetryable() }
): Flow<T> = retryWhen { cause, attempt ->
    if (attempt >= maxRetries || !shouldRetry(cause)) {
        false
    } else {
        delay(delayMillis)
        true
    }
}

/**
 * 判断异常是否可重试
 * 
 * 可重试的异常:
 * - IOException: 网络异常
 * - SocketTimeoutException: 超时
 * - ApiException: 特定的服务器错误码 (408, 429, 500, 502, 503, 504)
 * 
 * 不可重试的异常:
 * - UnknownHostException: DNS 解析失败
 * - ApiException: 客户端错误 (4xx,除了 408, 429)
 * - 其他未知异常
 * 
 * @return true 表示可以重试
 */
fun Throwable.isRetryable(): Boolean {
    return when (this) {
        // 网络异常 - 可重试
        is IOException -> {
            // DNS 解析失败不重试
            this !is UnknownHostException
        }
        
        // 超时异常 - 可重试
        is SocketTimeoutException -> true
        
        // 业务异常 - 根据错误码判断
        is ApiException -> {
            code in listOf(
                408,  // Request Timeout
                429,  // Too Many Requests
                500,  // Internal Server Error
                502,  // Bad Gateway
                503,  // Service Unavailable
                504   // Gateway Timeout
            )
        }
        
        // 其他异常 - 不重试
        else -> false
    }
}

/**
 * 判断异常是否为网络错误
 */
fun Throwable.isNetworkError(): Boolean {
    return this is IOException || this is SocketTimeoutException
}

/**
 * 判断异常是否为服务器错误
 */
fun Throwable.isServerError(): Boolean {
    return when (this) {
        is ApiException -> isServerError()
        else -> false
    }
}

/**
 * 判断异常是否为认证错误
 */
fun Throwable.isAuthError(): Boolean {
    return when (this) {
        is ApiException -> isAuthError()
        else -> false
    }
}
