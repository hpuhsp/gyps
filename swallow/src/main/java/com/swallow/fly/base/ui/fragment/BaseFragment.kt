package com.swallow.fly.base.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.snackbar.Snackbar
import com.swallow.fly.annotations.Stable
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.ui.activity.resolveViewModelClass
import com.swallow.fly.base.presentation.state.EventArgs
import com.swallow.fly.base.presentation.state.UiEvent
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.base.ui.delegate.PermissionDelegate
import com.swallow.fly.base.ui.delegate.PermissionDelegateImpl
import com.swallow.fly.base.ui.delegate.ProgressDelegate
import com.swallow.fly.base.ui.delegate.ProgressDelegateImpl
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * Fragment 基类，提供 MVVM 架构支持和常用功能封装
 *
 * 核心特性：
 * - **ViewModel 集成**：自动初始化和生命周期管理
 * - **ViewBinding 支持**：类型安全的视图访问
 * - **状态管理**：自动观察 [UiState] 和 [UiEvent]
 * - **委托模式**：通过 [ProgressDelegate] 和 [PermissionDelegate] 提供加载框和权限请求功能
 * - **EventBus 支持**：可选的事件总线集成
 *
 * 使用示例：
 * ```kotlin
 * @AndroidEntryPoint
 * class UserFragment : BaseFragment<UserViewModel, FragmentUserBinding>() {
 *
 *     override val modelClass: Class<UserViewModel> = UserViewModel::class.java
 *
 *     override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserBinding
 *         get() = FragmentUserBinding::inflate
 *
 *     override fun initView() {
 *         binding.btnLoad.setOnClickListener {
 *             mViewModel?.loadUserData()
 *         }
 *     }
 *
 *     override fun handleUiState(state: UiState) {
 *         when (state) {
 *             is UiState.Success<*> -> {
 *                 val user = state.data as? User
 *                 binding.tvName.text = user?.name
 *             }
 *             else -> super.handleUiState(state)
 *         }
 *     }
 * }
 * ```
 *
 * 生命周期说明：
 * - [onCreate]: 初始化 EventBus 和权限请求
 * - [onCreateView]: 初始化 ViewBinding 和 ViewModel
 * - [onViewCreated]: 开始观察 ViewModel 状态，调用 [initView]
 * - [onDestroy]: 清理资源，注销 EventBus
 *
 * 状态处理：
 * - [UiState.Loading]: 显示加载框
 * - [UiState.Success]: 隐藏加载框，子类可重写处理数据
 * - [UiState.Error]: 显示错误提示
 * - [UiState.Idle]: 隐藏加载框
 *
 * 自定义加载 UI：
 * 子类可以重写 [onStateLoading] 和 [onStateError] 来实现自定义的加载和错误 UI（如缺省页）
 *
 * @param VM ViewModel 类型，必须继承自 [BaseViewModel]
 * @param VB ViewBinding 类型
 *
 * @see BaseViewModel
 * @see UiState
 * @see UiEvent
 * @see ProgressDelegate
 * @see PermissionDelegate
 *
 * @since 1.0.0
 * @author Hsp
 */
@Stable
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> :
    Fragment(),
    IFragment,
    ProgressDelegate by ProgressDelegateImpl(),
    PermissionDelegate by PermissionDelegateImpl() {

    /**
     * ViewModel 的 Class 对象，用于自动初始化。
     *
     * 默认通过反射从泛型参数推断，无需子类显式声明。
     * 若推断失败（如泛型擦除场景），子类可手动覆盖：
     * ```kotlin
     * override val modelClass: Class<MyViewModel> = MyViewModel::class.java
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    open val modelClass: Class<VM>? by lazy {
        try { resolveViewModelClass(javaClass) as Class<VM> } catch (e: IllegalStateException) { null }
    }

    /**
     * ViewModel 实例，在 [onCreateView] 中自动初始化
     */
    @Nullable
    var mViewModel: VM? = null

    /**
     * ViewBinding 实例（内部使用）
     */
    private var _binding: ViewBinding? = null

    /**
     * ViewBinding 的 inflate 方法引用
     *
     * 子类必须提供 ViewBinding 的 inflate 方法：
     * ```kotlin
     * override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMyBinding
     *     get() = FragmentMyBinding::inflate
     * ```
     */
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    /**
     * ViewBinding 实例，提供类型安全的视图访问
     *
     * 在 [onViewCreated] 到 [onDestroyView] 之间可用
     */
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    /**
     * Context 实例，在 [onAttach] 中初始化
     */
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        // 初始化权限请求
        initPermissionLauncher(this) { onPermissionsResult(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        modelClass?.let { mViewModel = ViewModelProvider(this)[it] }
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (null != mViewModel) {
            initBaseActionEvent()
            observeViewModel()
        }
        initView()
    }

    /** 全局配置 */
    @SuppressLint("FragmentLiveDataObserve")
    private fun initBaseActionEvent() {
        mViewModel!!.pageStateEvent.observe(
            viewLifecycleOwner,
            Observer {
                when (it.event) {
                    EventArgs.SHOW_LOADING -> {
                        val msg = if (it.message != 0) getString(it.message) else null
                        showLoading(mContext, msg, it.cancelEnable)
                    }

                    EventArgs.DO_NOTHING, EventArgs.HIDE_DIALOG -> hideDialog()
                    EventArgs.SHOW_ERROR -> {
                        showToast(it.errorMsg)
                    }

                    EventArgs.SHOW_CONFIRM -> {
                        hideDialog()
                        showConfirmDialog(mContext, it.content, true)
                    }

                    EventArgs.SHOW_TOAST -> {
                        if (it.toastMsg.isNotEmpty()) {
                            showToast(it.toastMsg)
                        } else {
                            if (it.message != 0) {
                                showToast(getString(it.message))
                            }
                        }
                    }

                    else -> {}
                }
            }
        )
    }

    /**
     * 观察 ViewModel 的状态和事件
     *
     * 自动订阅 [BaseViewModel.uiState] 和 [BaseViewModel.uiEvent]，
     * 并在 [Lifecycle.State.STARTED] 状态下开始收集
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { mViewModel?.uiState?.collect { state -> handleUiState(state) } }
                launch { mViewModel?.uiEvent?.collect { event -> handleUiEvent(event) } }
            }
        }
    }

    /**
     * 处理 UI 状态
     *
     * 子类可以重写此方法来处理自定义状态：
     * ```kotlin
     * override fun handleUiState(state: UiState) {
     *     when (state) {
     *         is UiState.Success<*> -> {
     *             val data = state.data as? MyData
     *             // 处理数据
     *         }
     *         else -> super.handleUiState(state)
     *     }
     * }
     * ```
     *
     * @param state UI 状态
     */
    protected open fun handleUiState(state: UiState) {
        when (state) {
            is UiState.Init -> {} // Do nothing
            is UiState.Idle -> hideDialog()
            is UiState.Loading -> onStateLoading(state.message)
            is UiState.Success<*> -> onStateSuccess(state.data)
            is UiState.Error -> onStateError(state.message)
        }
    }

    /**
     * 处理加载状态
     *
     * 子类可重写此方法实现自定义加载 UI（如缺省页）：
     * ```kotlin
     * override fun onStateLoading(message: String?) {
     *     binding.emptyView.showLoading(message)
     * }
     * ```
     *
     * @param message 加载提示信息
     */
    protected open fun onStateLoading(message: String?) {
        showLoading(mContext, message, false)
    }

    /**
     * 处理成功状态
     *
     * 默认实现只是隐藏加载框，子类可重写处理数据
     *
     * @param data 成功返回的数据
     */
    protected open fun onStateSuccess(data: Any?) {
        hideDialog()
    }

    /**
     * 处理错误状态
     *
     * 子类可重写此方法实现自定义错误 UI（如缺省页）：
     * ```kotlin
     * override fun onStateError(message: String) {
     *     binding.emptyView.showError(message) {
     *         // 重试逻辑
     *     }
     * }
     * ```
     *
     * @param message 错误信息
     */
    protected open fun onStateError(message: String) {
        hideDialog()
        showToast(message)
    }

    /**
     * 处理 UI 事件
     *
     * 子类可以重写此方法来处理自定义事件：
     * ```kotlin
     * override fun handleUiEvent(event: UiEvent) {
     *     when (event) {
     *         is UiEvent.Navigate -> {
     *             // 处理导航
     *         }
     *         else -> super.handleUiEvent(event)
     *     }
     * }
     * ```
     *
     * @param event UI 事件
     */
    protected open fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowToast -> showToast(event.message)
            is UiEvent.ShowError -> showToast(event.message)
            is UiEvent.Navigate -> onNavigate(event)
            is UiEvent.NavigateBack -> onNavigateBack(event)
            is UiEvent.ShowDialog -> onShowDialog(event)
            is UiEvent.ShowSnackBar -> onShowSnackBar(event)
        }
    }

    /**
     * 处理导航事件
     * 
     * 默认实现使用 Deep Link 导航，子类可重写实现自定义导航逻辑
     * 
     * @param event 导航事件
     */
    protected open fun onNavigate(event: UiEvent.Navigate) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, event.route.toUri()).apply {
                event.args?.let { putExtras(it) }
                // 设置启动模式
                flags = event.launchMode.toIntentFlags()
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("导航失败")
        }
    }

    /**
     * 处理返回事件
     * 
     * @param event 返回事件
     */
    protected open fun onNavigateBack(event: UiEvent.NavigateBack) {
        activity?.let { activity ->
            event.result?.let {
                activity.setResult(Activity.RESULT_OK, Intent().putExtras(it))
            }
            activity.finish()
        }
    }

    /**
     * 处理显示对话框事件
     * 
     * @param event 对话框事件
     */
    protected open fun onShowDialog(event: UiEvent.ShowDialog) {
        androidx.appcompat.app.AlertDialog.Builder(mContext)
            .apply {
                event.title?.let { setTitle(it) }
                setMessage(event.message)
                event.positiveButton?.let { 
                    setPositiveButton(it) { dialog, _ ->
                        onDialogPositiveClick(event.tag)
                        dialog.dismiss()
                    }
                }
                event.negativeButton?.let {
                    setNegativeButton(it) { dialog, _ ->
                        onDialogNegativeClick(event.tag)
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 对话框确定按钮点击回调
     * 
     * @param tag 对话框标识
     */
    protected open fun onDialogPositiveClick(tag: String?) {
        // 子类实现
    }

    /**
     * 对话框取消按钮点击回调
     * 
     * @param tag 对话框标识
     */
    protected open fun onDialogNegativeClick(tag: String?) {
        // 子类实现
    }

    /**
     * 处理显示 SnackBar 事件
     * 
     * @param event SnackBar 事件
     */
    protected open fun onShowSnackBar(event: UiEvent.ShowSnackBar) {
        val duration = when (event.duration) {
            1 -> Snackbar.LENGTH_LONG
            -1 -> Snackbar.LENGTH_INDEFINITE
            else -> Snackbar.LENGTH_SHORT
        }
        
        val snackbar = Snackbar.make(
            requireView(),
            event.message,
            duration
        )
        
        event.actionText?.let { actionText ->
            snackbar.setAction(actionText) {
                onSnackBarAction(event.message)
            }
        }
        
        snackbar.show()
    }

    /**
     * SnackBar 操作按钮点击回调
     * 
     * @param message SnackBar 消息内容
     */
    protected open fun onSnackBarAction(message: String) {
        // 子类实现
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideDialog()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        hideDialog()
    }

    /*=======================================抽象方法==============================================*/

    /**
     * 初始化视图
     *
     * 在 [onViewCreated] 中调用，此时 [binding] 和 [mViewModel] 已经初始化完成
     *
     * 子类应在此方法中进行视图初始化、事件绑定等操作：
     * ```kotlin
     * override fun initView() {
     *     binding.btnSubmit.setOnClickListener {
     *         mViewModel?.submitData()
     *     }
     * }
     * ```
     */
    abstract fun initView()

    /*=======================================重写方法==============================================*/

    /**
     * 是否使用 EventBus
     *
     * 默认返回 false，子类可重写返回 true 以启用 EventBus
     *
     * @return true 表示使用 EventBus，false 表示不使用
     */
    override fun useEventBus(): Boolean {
        return false
    }

    /**
     * 权限请求结果回调
     *
     * 使用 [PermissionDelegate.requestPermissions] 请求权限后，结果会回调到此方法
     *
     * @param permissions 权限请求结果，key 为权限名称，value 为是否授予
     */
    open fun onPermissionsResult(permissions: Map<String, Boolean>) {}

    /** 显示进度框 (兼容旧 API) */
    open fun showLoading(msg: String?, cancelEnable: Boolean) {
        showLoading(mContext, msg, cancelEnable)
    }

    /** 显示确认弹框 (兼容旧 API) */
    open fun showConfirmDialog(message: String?, cancelEnable: Boolean) {
        showConfirmDialog(mContext, message, cancelEnable)
    }

    /** 显示确认弹框 (兼容旧 API) */
    open fun showConfirmDialog(message: String?, listener: DialogInterface.OnClickListener) {
        showConfirmDialog(mContext, message, listener)
    }

    /** 显示Toast */
    @SuppressLint("ShowToast")
    open fun showToast(message: CharSequence?) {
        message?.let { ToastUtils.showShort(it) }
    }

    /** 显示SnackBar */
    open fun makeSnackBar(view: View, message: CharSequence) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}
