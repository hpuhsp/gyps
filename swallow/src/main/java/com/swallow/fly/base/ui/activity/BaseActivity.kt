package com.swallow.fly.base.ui.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.swallow.fly.R
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.presentation.state.UiEvent
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.widget.CustomProgressDialog
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * @Description: 现代化 Activity 基类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 10:06
 * @UpdateRemark:   
 *   - 2024/12: 升级到现代化架构
 *   - 使用 by viewModels() 委托
 *   - 使用 Activity Result API
 *   - 使用 StateFlow/SharedFlow
 *   - 使用 repeatOnLifecycle
 *   - 移除 ProgressDialog，使用 Material Design
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity(), IActivity {
    
    /**
     * 基础动态权限分类
     */
    companion object {
        // 读写
        val STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // 相机访问
        val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // 通讯录
        val CONTACTS_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS
        )

        // 拨号
        val PHONE_CALL_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL
        )

        // 日历
        val CALENDAR_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )

        // 录音
        val RECORD_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        // 短信收发
        val SMS_PERMISSIONS = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
        )

        // 位置
        val GPS_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
        )
    }

    /**
     * ViewModel - 使用委托方式
     * 子类应该这样实现：
     * override val viewModel: MyViewModel by viewModels()
     */
    protected abstract val viewModel: VM

    /**
     * ViewBinding
     */
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    /**
     * 是否显示深色标题栏
     */
    private var showDarkBar: Boolean = true

    /**
     * 是否支持软件弹出,考虑对布局的影響
     */
    private var keyBordEnable: Boolean = false

    /**
     * Material Design 加载对话框
     */
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null

    /**
     * Activity Result API - 权限请求
     */
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        onPermissionsResult(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        beforehandInit()
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)

        initImmersionBar()
        initBaseDialog()
        observeViewModel()
        initView(savedInstanceState)
        initData(savedInstanceState)
    }

    private fun initBaseDialog() {
        // 使用 Material Design 对话框
        if (!showSystemProgress()) {
            // 自定义进度对话框
            loadingDialog = MaterialAlertDialogBuilder(this)
                .setView(R.layout.dialog_custom_progress)
                .setCancelable(false)
                .create()
        }
    }

    /**
     * 观察 ViewModel 的状态和事件
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 观察 UI 状态
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
                // 观察 UI 事件
                launch {
                    viewModel.uiEvent.collect { event ->
                        handleUiEvent(event)
                    }
                }
            }
        }
    }

    /**
     * 处理 UI 状态
     * 子类可以重写此方法来处理自定义状态
     */
    protected open fun handleUiState(state: UiState) {
        when (state) {
            is UiState.Idle -> hideDialog()
            is UiState.Loading -> showLoading(state.message)
            is UiState.Success<*> -> {
                hideDialog()
                // 子类处理成功状态
            }
            is UiState.Error -> {
                hideDialog()
                showToast(state.message)
            }
        }
    }

    /**
     * 处理 UI 事件
     * 子类可以重写此方法来处理自定义事件
     */
    protected open fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowToast -> showToast(event.message)
            is UiEvent.ShowError -> showToast(event.message)
            is UiEvent.Navigate -> {
                // 子类处理导航
            }
        }
    }

    /**
     * can override
     */
    open fun beforehandInit() {

    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)

    /**
     * 请求权限 - 使用 Activity Result API
     */
    protected fun requestPermissions(permissions: Array<String>) {
        permissionLauncher.launch(permissions)
    }

    /**
     * 权限请求结果回调
     * 子类重写此方法来处理权限结果
     */
    protected open fun onPermissionsResult(permissions: Map<String, Boolean>) {
        // 子类实现
    }

    /**
     * @Deprecated 使用 requestPermissions() 和 onPermissionsResult() 替代
     */
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

    /**
     * 可在子類中根据需求重写此方法
     */
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
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        loadingDialog?.dismiss()
        loadingDialog = null
        _binding = null
    }

/*======================================UI相关====================================================*/
    /**
     * 显示进度框 - Material Design
     */
    open fun showLoading(msg: String? = null, cancelEnable: Boolean = false) {
        if (loadingDialog == null) {
            loadingDialog = MaterialAlertDialogBuilder(this)
                .setView(R.layout.dialog_custom_progress)
                .setCancelable(cancelEnable)
                .create()
        }
        loadingDialog?.setCancelable(cancelEnable)
        // 如果需要显示消息，可以在这里设置
        loadingDialog?.show()
    }

    /**
     * 显示确认弹框
     */
    open fun showConfirmDialog(message: String?, cancelEnable: Boolean) {
        if (message.isNullOrEmpty()) {
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setCancelable(cancelEnable)
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ -> 
                dialog.dismiss() 
            }
            .show()
    }

    /**
     * 显示确认弹框
     */
    open fun showConfirmDialog(message: String?, listener: DialogInterface.OnClickListener) {
        if (message.isNullOrEmpty()) {
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm), listener)
            .show()
    }

    /**
     * 显示提示Dialog
     */
    open fun showTipsDialog(message: String?, cancelEnable: Boolean) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setCancelable(cancelEnable)
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ -> 
                dialog?.dismiss() 
            }
            .show()
    }

    /**
     * 隐藏进度框
     */
    open fun hideDialog() {
        loadingDialog?.dismiss()
    }

    /**
     * 显示Toast
     */
    open fun showToast(message: CharSequence?) {
        message?.let {
            if (message.toString().isNotBlank()) {
                ToastUtils.showShort(it)
            }
        }
    }

    /**
     * 显示Snackbar
     */
    private fun makeSnackBar(view: View, message: CharSequence) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private var mInputMethodManager: InputMethodManager? = null

    /**
     * 隱藏软键盘
     */
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

    /**
     * 获取App版本名
     */
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
            pi = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_CONFIGURATIONS
            )
            return pi
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pi
    }

    override fun useEventBus(): Boolean {
        return false
    }

    /**
     * 是否显示系统进度条控件，默认为false，显示自定义菊花转
     */
    override fun showSystemProgress(): Boolean {
        return false
    }
}
