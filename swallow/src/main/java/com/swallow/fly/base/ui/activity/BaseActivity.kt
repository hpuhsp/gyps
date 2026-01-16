package com.swallow.fly.base.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.swallow.fly.R
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.presentation.state.UiEvent
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.base.ui.delegate.PermissionDelegate
import com.swallow.fly.base.ui.delegate.PermissionDelegateImpl
import com.swallow.fly.base.ui.delegate.ProgressDelegate
import com.swallow.fly.base.ui.delegate.ProgressDelegateImpl
import com.therouter.TheRouter
import com.therouter.TheRouter.inject
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


/**
 * @Description: 现代化 Activity 基类
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/24 10:06
 * @UpdateRemark:
 * - 2024/12: 升级到现代化架构
 * - 使用 by viewModels() 委托
 * - 使用 Activity Result API
 * - 使用 StateFlow/SharedFlow
 * - 使用 repeatOnLifecycle
 * - 移除 ProgressDialog，使用 Material Design
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> :
    AppCompatActivity(),
    IActivity,
    ProgressDelegate by ProgressDelegateImpl(),
    PermissionDelegate by PermissionDelegateImpl() {

    /** ViewModel - 使用委托方式 子类应该这样实现： override val viewModel: MyViewModel by viewModels() */
    protected abstract val viewModel: VM

    /** ViewBinding */
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    /** 是否显示深色标题栏 */
    private var showDarkBar: Boolean = true

    /** 是否支持软件弹出,考虑对布局的影響 */
    private var keyBordEnable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }

        if (shouldInject()) {
            performInject()
        }

        beforehandInit()
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)

        // 初始化权限监听
        initPermissionLauncher(this) { permissions -> onPermissionsResult(permissions) }

        initImmersionBar()
        observeViewModel()
        initView(savedInstanceState)
        initData(savedInstanceState)
    }

    /** 观察 ViewModel 的状态和事件 */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 观察 UI 状态
                launch { viewModel.uiState.collect { state -> handleUiState(state) } }
                // 观察 UI 事件
                launch { viewModel.uiEvent.collect { event -> handleUiEvent(event) } }
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
        showLoading(this, message)
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
            is UiEvent.Navigate -> {
                // 子类处理导航
            }
        }
    }

    /** can override */
    open fun beforehandInit() {}

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)

    /** 权限请求结果回调 子类重写此方法来处理权限结果 */
    protected open fun onPermissionsResult(permissions: Map<String, Boolean>) {
        // 子类实现
    }

    /** @Deprecated 使用 requestPermissions() 和 onPermissionsResult() 替代 */
    @Deprecated(
        message = "Use requestPermissions() and onPermissionsResult() instead",
        replaceWith = ReplaceWith("requestPermissions(permissions)")
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun initImmersionBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(showDarkToolBar())
            .statusBarColor(getStatusBarColor())
            .keyboardEnable(keyBordEnable())
            .fullScreen(makeFullScreen())
            .init()
    }

    private fun getToolBar(): Toolbar? {
        return null
    }

    open fun getStatusBarColor(): Int {
        return R.color.gray
    }

    open fun makeFullScreen(): Boolean {
        return false
    }

    open fun hasActionBar(): Boolean {
        return true
    }

    /** 可在子類中根据需求重写此方法 */
    open fun showDarkToolBar(): Boolean {
        return showDarkBar
    }

    open fun keyBordEnable(): Boolean {
        return keyBordEnable
    }

    /*======================================生命周期相关===============================================*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // 隐藏软键盘
            hideSoftKeyBoard()
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        hideDialog()
        _binding = null
    }

    /*======================================UI相关====================================================*/
    /** 显示进度框 - 由代理实现 */
    open fun showLoading(msg: String? = null, cancelEnable: Boolean = false) {
        showLoading(this, msg, cancelEnable)
    }

    /** 显示Toast */
    open fun showToast(message: CharSequence?) {
        message?.let {
            if (message.toString().isNotBlank()) {
                ToastUtils.showShort(it)
            }
        }
    }

    /** 显示Snackbar */
    private fun makeSnackBar(view: View, message: CharSequence) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private var mInputMethodManager: InputMethodManager? = null

    /** 隱藏软键盘 */
    open fun hideSoftKeyBoard() {
        val localView = currentFocus
        if (mInputMethodManager == null) {
            mInputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        if (localView != null && mInputMethodManager != null) {
            mInputMethodManager?.hideSoftInputFromWindow(localView.windowToken, 2)
        }
    }
    /*======================================工具方法===================================================*/

    /** 获取App版本名 */
    fun getVersionName(): String? {
        return getPackageInfo(applicationContext)?.versionName ?: ""
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    private fun getPackageInfo(context: Context): PackageInfo? {
        var pi: PackageInfo? = null
        try {
            val pm = context.packageManager
            pi = pm.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
            return pi
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pi
    }

    fun performInject() {
        TheRouter.inject(this)
    }

    override fun useEventBus(): Boolean {
        return false
    }

    override fun shouldInject(): Boolean {
        return false
    }

    /** 是否显示系统进度条控件，默认为false，显示自定义菊花转 */
    override fun showSystemProgress(): Boolean {
        return false
    }
}
