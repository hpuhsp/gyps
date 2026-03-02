package com.swallow.fly.base.ui.activity

import android.app.Activity
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
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.swallow.fly.R
import com.swallow.fly.annotations.Stable
import com.swallow.fly.annotations.ScheduledForRemoval
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.presentation.state.UiEvent
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.base.ui.delegate.PermissionDelegate
import com.swallow.fly.base.ui.delegate.PermissionDelegateImpl
import com.swallow.fly.base.ui.delegate.ProgressDelegate
import com.swallow.fly.base.ui.delegate.ProgressDelegateImpl
import com.swallow.fly.ext.finishWithTransition
import com.therouter.TheRouter
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


/**
 * 现代化 Activity 基类
 * 
 * ## 功能特性
 * - 自动创建和管理 ViewModel（支持 Hilt 依赖注入）
 * - ViewBinding 支持
 * - StateFlow/SharedFlow 状态管理
 * - 权限请求代理
 * - 进度弹窗代理
 * - EventBus 可选支持
 * - ImmersionBar 沉浸式状态栏
 * 
 * ## 使用示例
 * ```kotlin
 * @AndroidEntryPoint
 * class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
 *
 *     override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
 *         get() = ActivityMainBinding::inflate
 *
 *     override val modelClass: Class<MainViewModel>
 *         get() = MainViewModel::class.java
 *
 *     override fun initView(savedInstanceState: Bundle?) {
 *         binding.button.setOnClickListener {
 *             mViewModel.loadData()
 *         }
 *     }
 *
 *     override fun initData(savedInstanceState: Bundle?) {
 *         mViewModel.loadData()
 *     }
 * }
 * ```
 * 
 * @param VM ViewModel 类型，必须继承自 [BaseViewModel]
 * @param VB ViewBinding 类型，必须实现 [ViewBinding]
 * 
 * @author Hsp
 * @since 1.0.0
 * @see BaseViewModel
 * @see BaseFragment
 */
@Stable
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> :
    AppCompatActivity(),
    IActivity,
    ProgressDelegate by ProgressDelegateImpl(),
    PermissionDelegate by PermissionDelegateImpl() {

    /**
     * ViewModel
     * 考虑Kotlin的扩展支持，下面方式更为方便。也可不对ViewModel以泛型进行基类封装
     *    private val loginViewModel by viewModels<LoginViewModel>()
     */
    abstract val modelClass: Class<VM>
    lateinit var mViewModel: VM

    /**
     * ViewBinding
     */
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    /** 是否显示深色标题栏 */
    private var showDarkBar: Boolean = true

    /** 是否支持软件弹出，考虑对布局的影响 */
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

        mViewModel = ViewModelProvider(this)[modelClass]
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
                launch { mViewModel.uiState.collect { state -> handleUiState(state) } }
                // 观察 UI 事件
                launch { mViewModel.uiEvent.collect { event -> handleUiEvent(event) } }
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

    /**
     * 处理 UI 事件
     * 
     * 子类可以重写此方法来处理自定义事件
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
        // 默认实现：可以根据项目需求自定义
        // 示例：使用 Deep Link 导航
        try {
            val intent = Intent(Intent.ACTION_VIEW, event.route.toUri()).apply {
                event.args?.let { putExtras(it) }
                // 设置启动模式
                flags = event.launchMode.toIntentFlags()
            }
            startActivity(intent)

            // 如果需要清空返回栈
            if (event.popUpTo != null && event.inclusive) {
                finish()
            }
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
        event.result?.let {
            setResult(RESULT_OK, Intent().putExtras(it))
        }
        finishWithTransition(R.anim.fade_in, R.anim.fade_out)
    }

    /**
     * 处理显示对话框事件
     * 
     * 默认使用 Material AlertDialog，子类可重写实现自定义对话框
     * 
     * @param event 对话框事件
     */
    protected open fun onShowDialog(event: UiEvent.ShowDialog) {
        androidx.appcompat.app.AlertDialog.Builder(this)
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
     * 子类可重写此方法处理对话框确定按钮点击
     * 
     * @param tag 对话框标识
     */
    protected open fun onDialogPositiveClick(tag: String?) {
        // 子类实现
    }

    /**
     * 对话框取消按钮点击回调
     * 
     * 子类可重写此方法处理对话框取消按钮点击
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
            findViewById(android.R.id.content),
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
     * 子类可重写此方法处理 SnackBar 操作按钮点击
     * 
     * @param message SnackBar 消息内容
     */
    protected open fun onSnackBarAction(message: String) {
        // 子类实现
    }

    /** can override */
    open fun beforehandInit() {}

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)

    /**
     * 权限请求结果回调
     *
     * 子类重写此方法来处理权限结果
     *
     * @param permissions 权限结果 Map，key 为权限名，value 为是否授予
     */
    protected open fun onPermissionsResult(permissions: Map<String, Boolean>) {
        // 子类实现
    }

    /**
     * @deprecated 使用 requestPermissions() 和 onPermissionsResult() 替代
     * @see requestPermissions
     * @see onPermissionsResult
     */
    @Deprecated(
        message = "Use requestPermissions() and onPermissionsResult() instead",
        replaceWith = ReplaceWith("requestPermissions(permissions)")
    )
    @ScheduledForRemoval(version = "3.0.0", replaceWith = "requestPermissions()")
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

    /** 可在子类中根据需求重写此方法 */
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
            finishWithTransition(R.anim.fade_in, R.anim.fade_out)
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

    /** 隐藏软键盘 */
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
