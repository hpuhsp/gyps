package com.swallow.fly.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Hilt ViewModel 扩展函数
 * 使用 Hilt 注入的 ViewModel
 */
@Composable
inline fun <reified VM : ViewModel> hiltViewModel(): VM {
    return androidx.hilt.navigation.compose.hiltViewModel()
}

/**
 * StateFlow 扩展函数，在 Compose 中收集状态
 * 自动处理生命周期
 *
 * @param lifecycleState 最小生命周期状态，默认为 STARTED
 * @param context 协程上下文
 * @return State<T>
 */
@Composable
fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val flowLifecycleAware = remember(this, lifecycleOwner) {
        this.flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
    }
    return flowLifecycleAware.collectAsState(initial = this.value, context = context)
}

/**
 * Flow 扩展函数，在 Compose 中收集状态
 * 自动处理生命周期
 *
 * @param initialValue 初始值
 * @param lifecycleState 最小生命周期状态，默认为 STARTED
 * @param context 协程上下文
 * @return State<T>
 */
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycle(
    initialValue: T,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val flowLifecycleAware = remember(this, lifecycleOwner) {
        this.flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
    }
    return flowLifecycleAware.collectAsState(initial = initialValue, context = context)
}
