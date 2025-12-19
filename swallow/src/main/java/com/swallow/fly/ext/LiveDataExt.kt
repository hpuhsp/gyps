package com.swallow.fly.ext

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * LiveData 扩展函数
 * 提供 LiveData 到 Flow 的转换和观察辅助函数
 */

/**
 * LiveData 转 Flow
 * 将 LiveData 转换为 Flow
 */
fun <T> LiveData<T>.asFlow(): Flow<T> = liveData {
    emitSource(this@asFlow)
}.asFlow()

/**
 * LiveData 转 StateFlow
 * 将 LiveData 转换为 StateFlow
 *
 * @param scope 协程作用域
 * @param initialValue 初始值
 */
fun <T> LiveData<T>.asStateFlow(
    scope: CoroutineScope,
    initialValue: T
): StateFlow<T> = asFlow().stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = initialValue
)

/**
 * Flow 转 LiveData
 * 将 Flow 转换为 LiveData
 */
fun <T> Flow<T>.asLiveData(): LiveData<T> = liveData {
    collect { value ->
        emit(value)
    }
}

/**
 * Flow 转 LiveData（带作用域）
 */
fun <T> Flow<T>.asLiveData(
    context: kotlin.coroutines.CoroutineContext = kotlinx.coroutines.Dispatchers.Main.immediate,
    timeoutInMs: Long = 5000
): LiveData<T> = liveData(context, timeoutInMs) {
    collect { value ->
        emit(value)
    }
}

/**
 * 观察 LiveData（简化版）
 * 自动使用 viewLifecycleOwner
 */
inline fun <T> LiveData<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    observe(owner) { value ->
        value?.let { observer(it) }
    }
}

/**
 * 观察 LiveData（非空值）
 * 只在值非空时才触发观察
 */
inline fun <T> LiveData<T>.observeNonNull(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    observe(owner) { value ->
        value?.let { observer(it) }
    }
}

/**
 * 观察 LiveData（一次性）
 * 只观察一次，收到值后自动移除观察者
 */
inline fun <T> LiveData<T>.observeOnce(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer(value)
            removeObserver(this)
        }
    })
}

/**
 * 映射 LiveData
 * 转换 LiveData 的值
 */
fun <T, R> LiveData<T>.map(transform: (T) -> R): LiveData<R> {
    return MediatorLiveData<R>().apply {
        addSource(this@map) { value ->
            this.value = transform(value)
        }
    }
}

/**
 * 切换映射 LiveData
 * 根据源 LiveData 的值切换到不同的 LiveData
 */
fun <T, R> LiveData<T>.switchMap(transform: (T) -> LiveData<R>): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var source: LiveData<R>? = null
        addSource(this@switchMap) { value ->
            source?.let { removeSource(it) }
            source = transform(value).also { newSource ->
                addSource(newSource) { this.value = it }
            }
        }
    }
}

/**
 * 过滤 LiveData
 * 只发射满足条件的值
 */
fun <T> LiveData<T>.filter(predicate: (T) -> Boolean): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@filter) { value ->
            if (predicate(value)) {
                this.value = value
            }
        }
    }
}

/**
 * 去重 LiveData
 * 只在值真正改变时才发射
 */
fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return MediatorLiveData<T>().apply {
        var lastValue: T? = null
        addSource(this@distinctUntilChanged) { value ->
            if (value != lastValue) {
                lastValue = value
                this.value = value
            }
        }
    }
}

/**
 * 组合两个 LiveData
 * 当任一 LiveData 发射新值时，使用最新的值组合
 */
fun <T1, T2, R> combineLiveData(
    source1: LiveData<T1>,
    source2: LiveData<T2>,
    combine: (T1?, T2?) -> R
): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var source1Value: T1? = null
        var source2Value: T2? = null

        addSource(source1) { value ->
            source1Value = value
            this.value = combine(source1Value, source2Value)
        }

        addSource(source2) { value ->
            source2Value = value
            this.value = combine(source1Value, source2Value)
        }
    }
}

/**
 * 组合三个 LiveData
 */
fun <T1, T2, T3, R> combineLiveData(
    source1: LiveData<T1>,
    source2: LiveData<T2>,
    source3: LiveData<T3>,
    combine: (T1?, T2?, T3?) -> R
): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var source1Value: T1? = null
        var source2Value: T2? = null
        var source3Value: T3? = null

        addSource(source1) { value ->
            source1Value = value
            this.value = combine(source1Value, source2Value, source3Value)
        }

        addSource(source2) { value ->
            source2Value = value
            this.value = combine(source1Value, source2Value, source3Value)
        }

        addSource(source3) { value ->
            source3Value = value
            this.value = combine(source1Value, source2Value, source3Value)
        }
    }
}

/**
 * 延迟发射
 * 延迟指定时间后发射值
 */
fun <T> LiveData<T>.debounce(duration: Long = 300): LiveData<T> {
    return MediatorLiveData<T>().apply {
        var job: kotlinx.coroutines.Job? = null

        addSource(this@debounce) { value ->
            job?.cancel()
            job = kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(duration)
                postValue(value)
            }
        }
    }
}

/**
 * 默认值
 * 当 LiveData 值为 null 时使用默认值
 */
fun <T> LiveData<T?>.withDefault(defaultValue: T): LiveData<T> {
    return map { it ?: defaultValue }
}
