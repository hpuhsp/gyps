package com.swallow.fly.http.ext

import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.http.result.HttpResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Flow 操作符扩展 - 丰富的组合操作
 * 
 * 提供常用的 Flow 组合操作符,简化开发:
 * - withLoadingState: 自动处理加载状态
 * - combineResults: 合并多个请求结果
 * - debounceFirst: 防抖
 * - throttleFirst: 节流
 * - timeoutWithFallback: 超时处理
 * 
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 2.0.0
 */

/**
 * 带加载状态的 Flow
 * 
 * 自动发射 Loading -> Success/Error 状态
 * 
 * 使用场景:
 * ```kotlin
 * // ViewModel 中
 * fun loadUser(userId: String) {
 *     viewModelScope.launch {
 *         repository.getUserInfo(userId)
 *             .withLoadingState()  // 自动处理加载状态
 *             .collect { state ->
 *                 _uiState.value = state
 *             }
 *     }
 * }
 * ```
 * 
 * @param loadingMessage 加载提示信息
 * @return Flow<UiState>
 */
fun <T> Flow<HttpResult<T>>.withLoadingState(
    loadingMessage: String? = null
): Flow<UiState> = flow {
    // 1. 发射加载状态
    emit(UiState.Loading(loadingMessage))
    
    // 2. 收集结果并转换为 UiState
    collect { result ->
        when (result) {
            is HttpResult.Success -> emit(UiState.Success(result.value))
            is HttpResult.Failure -> {
                val errorMessage = result.throwable?.message ?: "Unknown error"
                emit(UiState.Error(errorMessage))
            }
        }
    }
}

/**
 * 带加载状态的 Flow (自定义转换)
 * 
 * 允许自定义成功状态的转换逻辑
 * 
 * @param loadingMessage 加载提示信息
 * @param transform 成功时的转换函数
 * @return Flow<UiState>
 */
inline fun <T> Flow<HttpResult<T>>.withLoadingState(
    loadingMessage: String? = null,
    crossinline transform: (T) -> Any
): Flow<UiState> = flow {
    emit(UiState.Loading(loadingMessage))
    
    collect { result ->
        when (result) {
            is HttpResult.Success -> emit(UiState.Success(transform(result.value)))
            is HttpResult.Failure -> {
                val errorMessage = result.throwable?.message ?: "Unknown error"
                emit(UiState.Error(errorMessage))
            }
        }
    }
}

/**
 * 合并两个请求结果
 * 
 * 等待两个请求都完成后,合并结果
 * 
 * 使用场景:
 * ```kotlin
 * // ViewModel 中
 * fun loadUserProfile(userId: String) {
 *     viewModelScope.launch {
 *         combineResults(
 *             repository.getUserInfo(userId),
 *             repository.getUserPosts(userId)
 *         ) { user, posts ->
 *             UserProfile(user, posts)
 *         }.collect { result ->
 *             result.onSuccess { profile ->
 *                 _uiState.value = UiState.Success(profile)
 *             }
 *         }
 *     }
 * }
 * ```
 * 
 * @param flow1 第一个请求
 * @param flow2 第二个请求
 * @param transform 合并函数
 * @return 合并后的结果
 */
fun <T1, T2, R> combineResults(
    flow1: Flow<HttpResult<T1>>,
    flow2: Flow<HttpResult<T2>>,
    transform: (T1, T2) -> R
): Flow<HttpResult<R>> = flow1.combine(flow2) { result1, result2 ->
    when {
        result1 is HttpResult.Success && result2 is HttpResult.Success -> {
            HttpResult.Success(transform(result1.value, result2.value))
        }
        result1 is HttpResult.Failure -> result1
        result2 is HttpResult.Failure -> result2
        else -> HttpResult.Failure(Exception("Unknown error"))
    }
}

/**
 * 合并三个请求结果
 */
fun <T1, T2, T3, R> combineResults(
    flow1: Flow<HttpResult<T1>>,
    flow2: Flow<HttpResult<T2>>,
    flow3: Flow<HttpResult<T3>>,
    transform: (T1, T2, T3) -> R
): Flow<HttpResult<R>> = combine(flow1, flow2, flow3) { result1, result2, result3 ->
    when {
        result1 is HttpResult.Success && 
        result2 is HttpResult.Success && 
        result3 is HttpResult.Success -> {
            HttpResult.Success(transform(result1.value, result2.value, result3.value))
        }
        result1 is HttpResult.Failure -> result1
        result2 is HttpResult.Failure -> result2
        result3 is HttpResult.Failure -> result3
        else -> HttpResult.Failure(Exception("Unknown error"))
    }
}

/**
 * 防抖 - 短时间内只取最后一次发射
 * 
 * 使用场景: 搜索框输入
 * 
 * @param timeoutMillis 防抖时间窗口(毫秒)
 * @return 防抖后的 Flow
 */
fun <T> Flow<T>.debounceFirst(timeoutMillis: Long): Flow<T> = flow {
    var lastEmitTime = 0L
    
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmitTime >= timeoutMillis) {
            emit(value)
            lastEmitTime = currentTime
        }
    }
}

/**
 * 节流 - 固定时间间隔内只取第一次发射
 * 
 * 使用场景: 按钮点击防抖
 * 
 * @param windowDuration 时间窗口(毫秒)
 * @return 节流后的 Flow
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var windowStartTime = 0L
    
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - windowStartTime >= windowDuration) {
            emit(value)
            windowStartTime = currentTime
        }
    }
}

/**
 * 超时处理 - 提供默认值
 * 
 * 如果请求超时,返回默认值而不是抛出异常
 * 
 * @param timeoutMillis 超时时间(毫秒)
 * @param fallback 默认值
 * @return 带超时处理的 Flow
 */
fun <T> Flow<T>.timeoutWithFallback(
    timeoutMillis: Long,
    fallback: T
): Flow<T> = flow {
    withTimeoutOrNull(timeoutMillis) {
        collect { emit(it) }
    } ?: emit(fallback)
}

/**
 * 超时处理 - 提供默认值生成器
 * 
 * @param timeoutMillis 超时时间(毫秒)
 * @param fallback 默认值生成器
 * @return 带超时处理的 Flow
 */
inline fun <T> Flow<T>.timeoutWithFallback(
    timeoutMillis: Long,
    crossinline fallback: () -> T
): Flow<T> = flow {
    withTimeoutOrNull(timeoutMillis) {
        collect { emit(it) }
    } ?: emit(fallback())
}

/**
 * 在开始时发射初始值
 * 
 * 使用场景: 显示缓存数据后再请求网络
 * 
 * @param initialValue 初始值
 * @return 带初始值的 Flow
 */
fun <T> Flow<T>.startWithValue(initialValue: T): Flow<T> = onStart {
    emit(initialValue)
}

/**
 * 在开始时发射初始值 (HttpResult 版本)
 */
fun <T> Flow<HttpResult<T>>.startWithSuccess(initialValue: T): Flow<HttpResult<T>> = onStart {
    emit(HttpResult.Success(initialValue))
}
