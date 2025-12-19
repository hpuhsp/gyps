package com.swallow.fly.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

/**
 * Flow 扩展函数
 * 提供常用的 Flow 操作符和错误处理
 */

/**
 * 错误处理扩展
 * 捕获异常并转换为 Result
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> {
    Result.success(it)
}.catch { error ->
    Timber.e(error, "Flow error")
    emit(Result.failure(error))
}

/**
 * 错误处理扩展（带默认值）
 * 当发生错误时返回默认值
 */
fun <T> Flow<T>.catchWithDefault(defaultValue: T): Flow<T> = catch { error ->
    Timber.e(error, "Flow error, using default value")
    emit(defaultValue)
}

/**
 * 错误处理扩展（带错误处理器）
 * 当发生错误时执行指定的处理逻辑
 */
fun <T> Flow<T>.catchAndHandle(handler: suspend (Throwable) -> T): Flow<T> = catch { error ->
    Timber.e(error, "Flow error, handling")
    emit(handler(error))
}

/**
 * 网络错误重试
 * 仅对网络错误进行重试
 *
 * @param retries 重试次数
 * @param delayMillis 重试延迟（毫秒）
 */
fun <T> Flow<T>.retryOnNetworkError(
    retries: Long = 3,
    delayMillis: Long = 1000
): Flow<T> = retryWhen { cause, attempt ->
    if (cause is IOException && attempt < retries) {
        Timber.w("Network error, retrying (${attempt + 1}/$retries)")
        kotlinx.coroutines.delay(delayMillis * (attempt + 1))
        true
    } else {
        false
    }
}

/**
 * 指数退避重试
 * 使用指数退避策略进行重试
 *
 * @param maxRetries 最大重试次数
 * @param initialDelayMillis 初始延迟（毫秒）
 * @param maxDelayMillis 最大延迟（毫秒）
 * @param factor 退避因子
 */
fun <T> Flow<T>.retryWithExponentialBackoff(
    maxRetries: Long = 3,
    initialDelayMillis: Long = 1000,
    maxDelayMillis: Long = 10000,
    factor: Double = 2.0
): Flow<T> = retryWhen { cause, attempt ->
    if (attempt < maxRetries) {
        val delay = (initialDelayMillis * Math.pow(factor, attempt.toDouble()))
            .toLong()
            .coerceAtMost(maxDelayMillis)
        Timber.w(cause, "Retrying with exponential backoff (${attempt + 1}/$maxRetries), delay: ${delay}ms")
        kotlinx.coroutines.delay(delay)
        true
    } else {
        Timber.e(cause, "Max retries reached")
        false
    }
}

/**
 * 节流操作
 * 在指定时间窗口内只发射第一个值
 *
 * @param windowDuration 时间窗口（毫秒）
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}

/**
 * 防抖操作
 * 只在指定时间内没有新值时才发射最后一个值
 * 这是 debounce 的别名，提供更清晰的语义
 */
fun <T> Flow<T>.debounceFirst(timeoutMillis: Long): Flow<T> = debounce(timeoutMillis)

/**
 * 状态流扩展：只在值真正改变时才发射
 */
fun <T> Flow<T>.distinctUntilChanged(): Flow<T> = distinctUntilChanged()

/**
 * 在指定作用域中收集 Flow
 * 简化 Flow 收集的样板代码
 */
fun <T> Flow<T>.launchIn(
    scope: CoroutineScope,
    action: suspend (T) -> Unit
) {
    scope.launch {
        collect { value ->
            action(value)
        }
    }
}

/**
 * 安全收集 Flow
 * 自动处理异常
 */
suspend fun <T> Flow<T>.collectSafely(
    onError: (Throwable) -> Unit = { Timber.e(it, "Flow collection error") },
    action: suspend (T) -> Unit
) {
    catch { error ->
        onError(error)
    }.collect { value ->
        try {
            action(value)
        } catch (e: Exception) {
            onError(e)
        }
    }
}

/**
 * 转换为 StateFlow
 * 将普通 Flow 转换为 StateFlow
 */
fun <T> Flow<T>.stateIn(
    scope: CoroutineScope,
    initialValue: T
): StateFlow<T> = stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = initialValue
)

/**
 * 转换为 SharedFlow
 * 将普通 Flow 转换为 SharedFlow
 */
fun <T> Flow<T>.shareIn(
    scope: CoroutineScope,
    replay: Int = 0
): SharedFlow<T> = shareIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5000),
    replay = replay
)

/**
 * 组合多个 Flow
 * 当任一 Flow 发射新值时，使用最新的值组合
 */
fun <T1, T2, R> combineFlows(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R> = combine(flow1, flow2, transform)

/**
 * 组合三个 Flow
 */
fun <T1, T2, T3, R> combineFlows(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    transform: suspend (T1, T2, T3) -> R
): Flow<R> = combine(flow1, flow2, flow3, transform)

/**
 * 映射为 Result
 * 将 Flow<T> 映射为 Flow<Result<R>>
 */
fun <T, R> Flow<T>.mapToResult(transform: suspend (T) -> R): Flow<Result<R>> = map { value ->
    try {
        Result.success(transform(value))
    } catch (e: Exception) {
        Timber.e(e, "Transform error")
        Result.failure(e)
    }
}

/**
 * 过滤成功的 Result
 */
fun <T> Flow<Result<T>>.filterSuccess(): Flow<T> = mapNotNull { result ->
    result.getOrNull()
}

/**
 * 过滤失败的 Result
 */
fun <T> Flow<Result<T>>.filterFailure(): Flow<Throwable> = mapNotNull { result ->
    result.exceptionOrNull()
}
