package com.swallow.fly.base.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.snackbar.Snackbar
import com.swallow.fly.base.presentation.BaseViewModel
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
 * @Description: Fragment基类
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/24 10:06
 * @UpdateRemark: 更新说明：
 * - 2024/12: 采用 Delegate 模式重构，支持 ActivityResult API
 * - 引入 UiState/UiEvent 观察机制
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> :
        Fragment(),
        IFragment,
        ProgressDelegate by ProgressDelegateImpl(),
        PermissionDelegate by PermissionDelegateImpl() {

    /** ViewModel */
    abstract val modelClass: Class<VM>?

    @Nullable var mViewModel: VM? = null

    /** ViewBinding */
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

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
        modelClass?.let { mViewModel = ViewModelProvider(this).get(it) }
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
                this,
                Observer {
                    when (it.event) {
                        EventArgs.SHOW_LOADING ->
                                showLoading(mContext, getString(it.message), it.cancelEnable)
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

    /** 观察 ViewModel 的状态和事件 */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { mViewModel?.uiState?.collect { state -> handleUiState(state) } }
                launch { mViewModel?.uiEvent?.collect { event -> handleUiEvent(event) } }
            }
        }
    }

    /** 处理 UI 状态 */
    protected open fun handleUiState(state: UiState) {
        when (state) {
            is UiState.Init -> {} // Do nothing
            is UiState.Idle -> hideDialog()
            is UiState.Loading -> onStateLoading(state.message)
            is UiState.Success<*> -> onStateSuccess(state.data)
            is UiState.Error -> onStateError(state.message)
        }
    }

    /** 处理加载状态 子类可重写此方法实现自定义加载 UI (如缺省页) */
    protected open fun onStateLoading(message: String?) {
        showLoading(mContext, message, false)
    }

    /** 处理成功状态 */
    protected open fun onStateSuccess(data: Any?) {
        hideDialog()
    }

    /** 处理错误状态 子类可重写此方法实现自定义错误 UI (如缺省页) */
    protected open fun onStateError(message: String) {
        hideDialog()
        showToast(message)
    }

    /** 处理 UI 事件 子类可以重写此方法来处理自定义事件 */
    protected open fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowToast -> showToast(event.message)
            is UiEvent.ShowError -> showToast(event.message)
            is UiEvent.Navigate -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        hideDialog()
        _binding = null
    }

    /*=======================================抽象方法==============================================*/
    abstract fun initView()
    /*=======================================重写方法==============================================*/

    override fun useEventBus(): Boolean {
        return false
    }

    /** 权限请求结果 */
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
