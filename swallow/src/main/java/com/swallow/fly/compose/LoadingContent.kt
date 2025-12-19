package com.swallow.fly.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 通用的加载状态组件
 * 支持 Loading、Error、Success 状态
 *
 * @param T 数据类型
 * @param state UI 状态
 * @param onRetry 重试回调
 * @param modifier Modifier
 * @param loadingContent 自定义加载内容
 * @param errorContent 自定义错误内容
 * @param content 成功状态的内容
 */
@Composable
fun <T> LoadingContent(
    state: UiState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    errorContent: @Composable (String) -> Unit = { message ->
        DefaultErrorContent(message = message, onRetry = onRetry)
    },
    content: @Composable (T) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is UiState.Idle -> {
                // 空闲状态，不显示任何内容
            }
            is UiState.Loading -> {
                loadingContent()
            }
            is UiState.Success -> {
                content(state.data)
            }
            is UiState.Error -> {
                errorContent(state.message)
            }
        }
    }
}

/**
 * 默认加载内容
 */
@Composable
fun DefaultLoadingContent(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        if (message != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 默认错误内容
 */
@Composable
fun DefaultErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

/**
 * UI 状态密封类
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    data class Loading(val message: String? = null) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
