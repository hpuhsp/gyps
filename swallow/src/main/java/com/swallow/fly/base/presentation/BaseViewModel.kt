package com.swallow.fly.base.presentation

import androidx.lifecycle.*
import com.swallow.fly.base.ui.ViewBehavior
import com.swallow.fly.base.presentation.state.BaseStateEvent
import com.swallow.fly.base.presentation.state.EventArgs
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.base.presentation.state.UiEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @Description: 现代化 ViewModel 基类
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/8/24 10:25
 * @UpdateRemark:
 *   - 2024/12: 升级到现代化架构
 *   - 使用 StateFlow 替代 LiveData
 *   - 使用 SharedFlow 处理一次性事件
 *   - 添加结构化异常处理
 */
open class BaseViewModel : ViewModel(), ViewBehavior, LifecycleObserver {
    
    /**
     * UI 状态 - 使用 StateFlow
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    /**
     * UI 事件 - 使用 SharedFlow (一次性事件)
     */
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
    
    /**
     * 兼容旧代码 - 保留 LiveData 支持
     * @Deprecated 使用 uiState 和 uiEvent 替代
     */
    @Deprecated("Use uiState and uiEvent instead")
    private val _pageStateEvent = MutableLiveData<BaseStateEvent>()
    
    @Deprecated("Use uiState and uiEvent instead")
    val pageStateEvent: LiveData<BaseStateEvent> = _pageStateEvent

    val _failure = MutableLiveData<String>()
    val failure = _failure

    /**
     * 显示加载状态
     */
    protected fun showLoading(message: String? = null) {
        _uiState.value = UiState.Loading(message)
    }

    /**
     * 隐藏加载状态
     */
    protected fun hideLoading() {
        _uiState.value = UiState.Idle
    }

    /**
     * 显示错误
     */
    protected fun showError(error: Throwable) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowError(error.message ?: "Unknown error"))
        }
    }

    /**
     * 显示错误消息
     */
    protected fun showError(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowError(message))
        }
    }

    /**
     * 显示 Toast 消息（字符串）
     */
    override fun showToast(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowToast(message))
        }
    }
    
    /**
     * 显示 Toast 消息（资源 ID）
     */
    override fun showToast(message: Int) {
        // 资源 ID 需要在 View 层转换为字符串
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowToast(message.toString()))
        }
    }

    /**
     * 结构化异常处理
     */
    protected fun <T> Flow<T>.handleErrors(): Flow<T> = catch { error ->
        handleError(error)
    }

    private fun handleError(error: Throwable) {
        showError(error)
    }

    // ========== 兼容旧代码的方法 ==========
    
    @Deprecated("Use showLoading() instead")
    override fun showLoading(msg: Int) {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.SHOW_LOADING, message = msg)
    }

    @Deprecated("Use showLoading() instead")
    override fun showLoading(msg: Int, cancelEnable: Boolean) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_LOADING,
            message = msg,
            cancelEnable = cancelEnable
        )
    }

    @Deprecated("Use uiEvent instead")
    override fun showConfirmDialog(content: String) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_CONFIRM,
            content = content
        )
    }

    @Deprecated("Use hideLoading() instead")
    override fun hideAllDialog() {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.HIDE_DIALOG)
    }

    @Deprecated("Use showError() instead")
    override fun showError(code: Int, message: String?) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_ERROR,
            code = code,
            errorMsg = message
        )
    }

    override fun startActivity(cls: Class<*>?) {

    }

    override fun killApp() {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.KILL_APP)
    }
}
