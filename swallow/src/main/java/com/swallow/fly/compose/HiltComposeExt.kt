package com.swallow.fly.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Hilt Compose 集成扩展
 */

/**
 * 获取 Hilt 注入的 ViewModel
 * 这是 hiltViewModel() 的别名，提供更清晰的语义
 *
 * @param viewModelStoreOwner ViewModel 存储所有者，默认使用当前的 ViewModelStoreOwner
 * @return ViewModel 实例
 */
@Composable
inline fun <reified VM : ViewModel> getHiltViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
): VM {
    return androidx.hilt.navigation.compose.hiltViewModel(viewModelStoreOwner)
}

/**
 * 使用示例：
 *
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val viewModel: MyViewModel = hiltViewModel()
 *     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 *
 *     LoadingContent(
 *         state = uiState,
 *         onRetry = { viewModel.retry() }
 *     ) { data ->
 *         // 显示数据
 *     }
 * }
 * ```
 *
 * 或者使用 getHiltViewModel：
 *
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val viewModel: MyViewModel = getHiltViewModel()
 *     // ...
 * }
 * ```
 */
