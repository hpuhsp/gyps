package com.swallow.fly.base.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.swallow.fly.R
import com.swallow.fly.base.IView
import com.swallow.fly.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * @Description: 不适用ViewBinding,直接使用Kotlin扩展
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/4 11:45
 * @UpdateRemark:   更新说明：
 */
abstract class FastBaseActivity<VM : BaseViewModel> : AppCompatActivity(), IView {

    /**
     * 定义权限
     */
    companion object {
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

    @Nullable
    var mViewModel: VM? = null
//    lateinit var binding: VB

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
//        try {
//            // 采用反射
//            val type = javaClass.genericSuperclass
//            if (type is ParameterizedType) {
//                val clazz = type.actualTypeArguments[1] as Class<VB>
//                val method = clazz.getMethod("inflate", LayoutInflater::class.java)
//                binding = method.invoke(null, layoutInflater) as VB
//                setContentView(binding.root)
//            } else {
//                setContentView(getLayoutRes())
//            }
//            createViewModel()
//        } catch (e: Exception) {
//            if (e is InflateException) throw e
//            e.printStackTrace()
//        }
        createViewModel()
        initImmersionBar()
        initBaseActionEvent()
        initView(savedInstanceState)
        initData(savedInstanceState)
    }

    private fun createViewModel() {
        val genericSuperclass: Type? = javaClass.genericSuperclass
        if (genericSuperclass != null) {
            val parameterizedType: ParameterizedType = genericSuperclass as ParameterizedType
            val actualTypeArguments: Array<Type> = parameterizedType.actualTypeArguments
            val homeViewModelClass =
                actualTypeArguments[0] as Class<VM>
            mViewModel = ViewModelProvider(this).get(homeViewModelClass)
        }
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)

    /**
     * 全局配置
     */
    private fun initBaseActionEvent() {
        mViewModel?.pageStateEvent?.observe(this, Observer {
//            when (it.event) {
//                EventArgs.SHOW_LOADING -> showLoading(getString(it.message))
//                EventArgs.DO_NOTHING, EventArgs.HIDE_DIALOG -> hideDialog()
//                EventArgs.SHOW_ERROR -> showToast(it.errorMsg)
//                else -> {
//
//                }
//            }
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

    override fun finish() {
        super.finish()
        hideSoftKeyBoard()
    }
    //================================基类方法================================================
    /**
     * 显示进度框
     */
    private fun showLoading(msg: String?) {
        if (null == loadingDialog) {
            loadingDialog = ProgressDialog(this@FastBaseActivity)
        }
        if (!msg.isNullOrEmpty()) {
            loadingDialog?.setMessage(msg)
        }
        loadingDialog?.show()
    }

    /**
     * 隐藏进度框
     */
    private fun hideDialog() {
        loadingDialog?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != loadingDialog) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    /**
     * 显示Toast
     */
    @SuppressLint("ShowToast")
    fun showToast(message: CharSequence) {
        if (mToast == null) {
            mToast = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
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
}