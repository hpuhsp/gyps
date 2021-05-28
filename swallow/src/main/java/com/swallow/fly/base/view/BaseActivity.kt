package com.swallow.fly.base.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
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
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.swallow.fly.R
import com.swallow.fly.base.IActivity
import com.swallow.fly.base.event.EventArgs
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.widget.CustomProgressDialog
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

/**
 * @Description: 普通Activity基类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 10:06
 * @UpdateRemark:   更新说明：
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

    /**
     * 是否显示深色标题栏
     */
    private var showDarkBar: Boolean = true

    /**
     * 是否支持软件弹出,考虑对布局的影響
     */
    private var keyBordEnable: Boolean = false

    /**
     * 可进行扩展为自定义Dialog
     */
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        beforehandInit()
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        mViewModel = ViewModelProvider(this).get(modelClass)

        initImmersionBar()
        initBaseDialog()
        initBaseActionEvent()
        initView(savedInstanceState)
        initData(savedInstanceState)
    }

    private fun initBaseDialog() {
        loadingDialog = if (showSystemProgress()) {
            ProgressDialog(this)
        } else {
            CustomProgressDialog(this@BaseActivity)
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
     * 全局配置
     */
    private fun initBaseActionEvent() {
        mViewModel.pageStateEvent.observe(this, Observer {
            when (it.event) {
                EventArgs.SHOW_LOADING -> {
                    if (it.message == -1) {
                        showLoading("", it.cancelEnable)
                    } else {
                        showLoading(getString(it.message), it.cancelEnable)
                    }
                }
                EventArgs.DO_NOTHING, EventArgs.HIDE_DIALOG -> hideDialog()
                EventArgs.SHOW_ERROR -> {
                    hideDialog()
                    showToast(it.errorMsg)
                }
                EventArgs.SHOW_CONFIRM -> {
                    showConfirmDialog(it.content, true)
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
                else -> {

                }
            }
        })
    }

    private fun initImmersionBar() {
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

    private fun hasActionBar(): Boolean {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        loadingDialog.dismiss()
        _binding = null
    }

/*======================================UI相关====================================================*/
    /**
     * 显示进度框
     */
    open fun showLoading(msg: String?, cancelEnable: Boolean) {
        loadingDialog.setCancelable(cancelEnable)
        if (loadingDialog is ProgressDialog) {
            (loadingDialog as ProgressDialog).setMessage(msg ?: "")
        } else if (loadingDialog is CustomProgressDialog) {
            (loadingDialog as CustomProgressDialog).setMessage(msg)
        }
        loadingDialog.show()
    }

    /**
     * 显示确认弹框
     */
    open fun showConfirmDialog(message: String?, cancelEnable: Boolean) {
        if (message.isNullOrEmpty()) {
            return
        }
        AlertDialog.Builder(this).setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setCancelable(cancelEnable)
            .setPositiveButton(
                getString(R.string.confirm)
            ) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    /**
     * 显示确认弹框
     */
    open fun showConfirmDialog(message: String?, listener: DialogInterface.OnClickListener) {
        if (message.isNullOrEmpty()) {
            return
        }
        AlertDialog.Builder(this).setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm), listener)
            .create().show()
    }

    /**
     * 显示提示Dialog
     */
    open fun showTipsDialog(message: String?, cancelEnable: Boolean) {
        AlertDialog.Builder(this).setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setCancelable(cancelEnable)
            .setPositiveButton(
                getString(R.string.confirm)
            ) { dialog, _ -> dialog?.dismiss() }
            .create().show()
    }

    /**
     * 隐藏进度框
     */
    open fun hideDialog() {
        loadingDialog.dismiss()
    }


    /**
     * 显示Toast
     */
    open fun showToast(message: CharSequence?) {
        message?.let {
            if (message.toString().isNullOrBlank()) {
                return
            }
            ToastUtils.showShort(it)
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




