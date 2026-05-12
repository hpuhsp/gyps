package com.swallow.fly.base.presentation

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swallow.fly.annotations.ScheduledForRemoval
import com.swallow.fly.annotations.Stable
import com.swallow.fly.base.presentation.state.BaseStateEvent
import com.swallow.fly.base.presentation.state.EventArgs
import com.swallow.fly.base.presentation.state.LaunchMode
import com.swallow.fly.base.presentation.state.UiEvent
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.base.ui.ViewBehavior
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

/**
 * 现代化 ViewModel 基类
 * 
 * ## 功能特性
 * - StateFlow 状态管理（替代 LiveData）
 * - SharedFlow 事件管理（一次性事件）
 * - 结构化异常处理
 * - 自动生命周期管理
 * 
 * ## 使用示例
 * ```kotlin
 * @HiltViewModel
 * class MainViewModel @Inject constructor(
 *     private val repository: MainRepository
 * ) : BaseViewModel() {
 *     
 *     fun loadData() {
 *         viewModelScope.launch {
 *             repository.getData()
 *                 .onStart { showLoading("加载中...") }
 *                 .catch { showError(it) }
 *                 .onCompletion { hideLoading() }
 *                 .collect { result ->
 *                     result.onSuccess { data ->
 *                         _uiState.value = UiState.Success(data)
 *                     }
 *                 }
 *         }
 *     }
 * }
 * ```
 * 
 * ## 状态观察
 * ```kotlin
 * // 在 Activity/Fragment 中
 * lifecycleScope.launch {
 *     repeatOnLifecycle(Lifecycle.State.STARTED) {
 *         launch { viewModel.uiState.collect { state -> handleUiState(state) } }
 *         launch { viewModel.uiEvent.collect { event -> handleUiEvent(event) } }
 *     }
 * }
 * ```
 * 
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 1.0.0
 * @see UiState
 * @see UiEvent
 */
@Stable
open class BaseViewModel : ViewModel(), ViewBehavior {
    
    /**
     * UI 状态 - 使用 StateFlow
     * 
     * 性能优化：
     * - 使用 distinctUntilChanged 避免重复发射相同状态
     * - 使用 stateIn 确保状态持久化
     * 
     * @since 2.0.0
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Init)
    
    /**
     * 公开的 UI 状态流
     * 
     * StateFlow 本身已经具有去重功能，无需额外调用 distinctUntilChanged
     */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    /**
     * UI 事件 - 使用 SharedFlow (一次性事件)
     * 
     * 性能优化：
     * - replay = 0: 不缓存历史事件
     * - extraBufferCapacity = 1: 缓冲一个事件，避免丢失
     * - onBufferOverflow = DROP_OLDEST: 缓冲区满时丢弃最旧的事件
     * 
     * @since 2.0.0
     */
    private val _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1,  // 性能优化：避免事件丢失
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    /**
     * 公开的 UI 事件流
     */
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
    
    /**
     * 兼容旧代码 - 保留 LiveData 支持
     * 
     * @deprecated 使用 uiState 和 uiEvent 替代
     * @see uiState
     * @see uiEvent
     */
    @Deprecated(
        message = "Use uiState and uiEvent instead",
        replaceWith = ReplaceWith("uiState and uiEvent")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "uiState/uiEvent")
    private val _pageStateEvent = MutableLiveData<BaseStateEvent>()
    
    @Deprecated(
        message = "Use uiState and uiEvent instead",
        replaceWith = ReplaceWith("uiState and uiEvent")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "uiState/uiEvent")
    val pageStateEvent: LiveData<BaseStateEvent> = _pageStateEvent

    @Deprecated(
        message = "Use uiEvent (UiEvent.ShowError) instead",
        replaceWith = ReplaceWith("_uiEvent.tryEmit(UiEvent.ShowError(message))")
    )
    private val _failure = MutableLiveData<String>()

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use uiEvent (UiEvent.ShowError) instead",
        replaceWith = ReplaceWith("uiEvent")
    )
    val failure: LiveData<String> = _failure

    /**
     * 显示加载状态
     * 
     * 性能优化：只在状态真正变化时才更新
     * 
     * @param message 加载提示信息
     */
    protected fun showLoading(message: String? = null) {
        val newState = UiState.Loading(message)
        // 性能优化：避免重复更新相同状态
        if (_uiState.value != newState) {
            _uiState.value = newState
        }
    }

    /**
     * 隐藏加载状态
     */
    protected fun hideLoading() {
        val newState = UiState.Idle
        if (_uiState.value != newState) {
            _uiState.value = newState
        }
    }

    /**
     * 显示错误（Throwable）
     * 
     * @param error 异常对象
     */
    protected fun showError(error: Throwable) {
        // 性能优化：使用 tryEmit 避免挂起
        _uiEvent.tryEmit(UiEvent.ShowError(error.message ?: "Unknown error"))
    }

    /**
     * 显示错误消息
     * 
     * @param message 错误信息
     */
    protected fun showError(message: String) {
        _uiEvent.tryEmit(UiEvent.ShowError(message))
    }

    /**
     * 显示 Toast 消息（字符串）
     * 
     * @param message 提示信息
     */
    override fun showToast(message: String) {
        _uiEvent.tryEmit(UiEvent.ShowToast(message))
    }
    
    /**
     * 显示 Toast 消息（资源 ID）
     *
     * 注意：ViewModel 无法访问 Context，无法解析资源 ID。
     * 请在 View 层解析后调用 showToast(String)，或直接在 View 层显示 Toast。
     */
    @Deprecated(
        message = "ViewModel cannot resolve string resources. Resolve the resource ID in View layer and call showToast(String) instead.",
        replaceWith = ReplaceWith("showToast(context.getString(message))")
    )
    override fun showToast(message: Int) {
        // 不实现：ViewModel 无 Context，无法解析资源 ID，避免显示数字字符串给用户
    }

    // ========== 导航相关方法 ==========

    /**
     * 导航到指定路由
     * 
     * 使用示例：
     * ```kotlin
     * // 简单导航
     * navigate("/home")
     * 
     * // 带参数导航
     * navigate("/user/123", Bundle().apply {
     *     putString("name", "John")
     * })
     * 
     * // 使用 singleTop 模式
     * navigate("/home", launchMode = LaunchMode.SINGLE_TOP)
     * 
     * // 清空返回栈导航
     * navigate("/login", popUpTo = "/", inclusive = true)
     * ```
     * 
     * @param route 导航路由
     * @param args 导航参数
     * @param launchMode Activity 启动模式（默认 STANDARD）
     * @param popUpTo 返回到指定路由（清空返回栈）
     * @param inclusive 是否包含 popUpTo 的目标
     */
    protected fun navigate(
        route: String,
        args: Bundle? = null,
        launchMode: LaunchMode = LaunchMode.STANDARD,
        popUpTo: String? = null,
        inclusive: Boolean = false
    ) {
        _uiEvent.tryEmit(
            UiEvent.Navigate(
                route = route,
                args = args,
                launchMode = launchMode,
                popUpTo = popUpTo,
                inclusive = inclusive
            )
        )
    }

    /**
     * 返回上一页
     * 
     * 使用示例：
     * ```kotlin
     * // 简单返回
     * navigateBack()
     * 
     * // 带返回结果
     * navigateBack(Bundle().apply {
     *     putString("result", "success")
     * })
     * ```
     * 
     * @param result 返回结果
     */
    protected fun navigateBack(result: Bundle? = null) {
        _uiEvent.tryEmit(UiEvent.NavigateBack(result))
    }

    /**
     * 显示对话框
     * 
     * 使用示例：
     * ```kotlin
     * showDialog(
     *     title = "提示",
     *     message = "确定要删除吗？",
     *     positiveButton = "确定",
     *     negativeButton = "取消",
     *     tag = "delete_confirm"
     * )
     * ```
     * 
     * @param title 标题
     * @param message 消息内容
     * @param positiveButton 确定按钮文本
     * @param negativeButton 取消按钮文本
     * @param tag 对话框标识
     */
    protected fun showDialog(
        title: String? = null,
        message: String,
        positiveButton: String? = "确定",
        negativeButton: String? = null,
        tag: String? = null
    ) {
        _uiEvent.tryEmit(
            UiEvent.ShowDialog(
                title = title,
                message = message,
                positiveButton = positiveButton,
                negativeButton = negativeButton,
                tag = tag
            )
        )
    }

    /**
     * 显示 SnackBar
     * 
     * 使用示例：
     * ```kotlin
     * // 简单提示
     * showSnackBar("操作成功")
     * 
     * // 带操作按钮
     * showSnackBar("已删除", actionText = "撤销")
     * ```
     * 
     * @param message 消息内容
     * @param actionText 操作按钮文本
     * @param duration 显示时长（0: SHORT, 1: LONG, -1: INDEFINITE）
     */
    protected fun showSnackBar(
        message: String,
        actionText: String? = null,
        duration: Int = 0
    ) {
        _uiEvent.tryEmit(
            UiEvent.ShowSnackBar(
                message = message,
                actionText = actionText,
                duration = duration
            )
        )
    }

    /**
     * 结构化异常处理扩展
     * 
     * 使用示例：
     * ```kotlin
     * repository.getData()
     *     .handleErrors()  // 自动处理异常
     *     .collect { result -> }
     * ```
     * 
     * @return 处理异常后的 Flow
     */
    protected fun <T> Flow<T>.handleErrors(): Flow<T> = catch { error ->
        handleError(error)
    }

    /**
     * 内部错误处理
     */
    private fun handleError(error: Throwable) {
        showError(error)
    }

    // ========== 兼容旧代码的方法 ==========
    
    @Deprecated(
        message = "Use showLoading(message: String?) instead",
        replaceWith = ReplaceWith("showLoading(getString(msg))")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "showLoading(String)")
    override fun showLoading(msg: Int) {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.SHOW_LOADING, message = msg)
    }

    @Deprecated(
        message = "Use showLoading(message: String?) instead",
        replaceWith = ReplaceWith("showLoading(getString(msg))")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "showLoading(String)")
    override fun showLoading(msg: Int, cancelEnable: Boolean) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_LOADING,
            message = msg,
            cancelEnable = cancelEnable
        )
    }

    @Deprecated(
        message = "Use uiEvent instead",
        replaceWith = ReplaceWith("_uiEvent.emit(UiEvent.ShowDialog(content))")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "uiEvent")
    override fun showConfirmDialog(content: String) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_CONFIRM,
            content = content
        )
    }

    @Deprecated(
        message = "Use hideLoading() instead",
        replaceWith = ReplaceWith("hideLoading()")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "hideLoading()")
    override fun hideAllDialog() {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.HIDE_DIALOG)
    }

    @Deprecated(
        message = "Use showError(message: String) instead",
        replaceWith = ReplaceWith("showError(message ?: \"\")")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "showError(String)")
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
