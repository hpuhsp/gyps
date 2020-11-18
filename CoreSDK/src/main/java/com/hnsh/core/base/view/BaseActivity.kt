package com.hnsh.core.base.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.InflateException
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
import com.github.lilei.coroutinepermissions.requestPermissionsForResult
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.hnsh.core.R
import com.hnsh.core.base.IActivity
import com.hnsh.core.base.event.EventArgs
import com.hnsh.core.base.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
     * 定义权限
     */
    companion object {

        val STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permsContact = arrayOf(
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS
        )
        val permsPhone = arrayOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL
        )
        val permsCalendar = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
        val permsCamera = arrayOf(Manifest.permission.CAMERA)
        val permsSensors = arrayOf(Manifest.permission.BODY_SENSORS)
        val permsLocation = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val permsStorage = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permsMicoraphone = arrayOf(Manifest.permission.RECORD_AUDIO)
        val permsSms = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
        )

        var mToast: Toast? = null
    }

    open fun getLayoutRes(): Int {
        return 0
    }

    lateinit var binding: VB

    /**
     * 考虑Kotlin的扩展支持，下面方式更为方便。也可不对ViewModel以泛型进行基类封装
     *    private val loginViewModel by viewModels<LoginViewModel>()
     */
    abstract val modelClass: Class<VM>

    lateinit var mViewModel: VM

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
    private var loadingDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        beforehandInit()
        try {
            // 采用反射
            val type = javaClass.genericSuperclass
            if (type is ParameterizedType) {
                val clazz = type.actualTypeArguments[1] as Class<*>
                val method = clazz.getMethod("inflate", LayoutInflater::class.java)
                binding = method.invoke(null, layoutInflater) as VB
                setContentView(binding.root)
            } else {
                setContentView(getLayoutRes())
            }
        } catch (e: Exception) {
//            if (e is InflateException) throw e
            e.printStackTrace()
        }

        mViewModel = ViewModelProvider(this).get(modelClass)

        initImmersionBar()
        initBaseActionEvent()
        initView(savedInstanceState)
        initData(savedInstanceState)
    }

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
                EventArgs.SHOW_LOADING -> showLoading(getString(it.message))
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

    /**
     * ==============================权限相关===================================
     */
    // 请求权限
    open fun requestPermission(array: Array<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                requestPermissionsForResult(*permsStorage, rationale = "为了更好的提供服务，需要获取存储空间权限")
//                startActivity(
//                    Intent(
//                        Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    )
//                )
            } catch (e: Exception) {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //  TODO 根据权限怎么处理？
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {


        }
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

    //////////////////////////////////////////////////////////////////////////
    //                                                                      //
    //                   公用UI交互组件                                      //
    //                                                                      //
    //////////////////////////////////////////////////////////////////////////
    /**
     * 显示进度框
     */
    private fun showLoading(msg: String?) {
        if (null == loadingDialog) {
            loadingDialog = ProgressDialog(this@BaseActivity)
        }
        if (!msg.isNullOrEmpty()) {
            loadingDialog?.setMessage(msg)
        }
        loadingDialog?.show()
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
     * 隐藏进度框
     */
    fun hideDialog() {
        loadingDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        if (null != loadingDialog) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    /**
     * 显示Toast
     */
    @SuppressLint("ShowToast")
    open fun showToast(message: CharSequence?) {
        if (mToast == null) {
            mToast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        }
        if (message.isNullOrEmpty()) {
            return
        }
        mToast?.setText(message)
        mToast?.show()
    }

    /**
     * 显示Snackbar
     */
    private fun makeSnackbar(view: View, message: CharSequence) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private var mInputMethodManager: InputMethodManager? = null

    /**
     * 隱藏软键盘
     */
    fun hideSoftKeyBoard() {
        val localView = currentFocus
        if (mInputMethodManager == null) {
            mInputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        if (localView != null && mInputMethodManager != null) {
            mInputMethodManager?.hideSoftInputFromWindow(localView.windowToken, 2)
        }
    }

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
}




