package com.swallow.fly.base.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.snackbar.Snackbar
import com.swallow.fly.R
import com.swallow.fly.base.IFragment
import com.swallow.fly.base.event.EventArgs
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.widget.CustomProgressDialog
import org.greenrobot.eventbus.EventBus
import java.lang.Exception
import java.lang.reflect.ParameterizedType

/**
 * @Description: Fragment基类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 10:06
 * @UpdateRemark:   更新说明：
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment(), IFragment {

    /**
     * ViewModel
     */
    abstract val modelClass: Class<VM>

    private lateinit var mViewModel: VM

    /**
     * ViewBinding
     */
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    lateinit var mContext: Context

    /**
     * 可进行扩展为自定义Dialog
     */
    private lateinit var loadingDialog: Dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        mViewModel = ViewModelProvider(this).get(modelClass)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBaseDialog()
        initBaseActionEvent()
        initView()
    }

    /**
     * 全局配置
     */
    @SuppressLint("FragmentLiveDataObserve")
    private fun initBaseActionEvent() {
        mViewModel.pageStateEvent.observe(this, Observer {
            when (it.event) {
                EventArgs.SHOW_LOADING -> showLoading(getString(it.message), it.cancelEnable)
                EventArgs.DO_NOTHING, EventArgs.HIDE_DIALOG -> hideDialog()
                EventArgs.SHOW_ERROR -> {
                    showToast(it.errorMsg)
                }
                EventArgs.SHOW_CONFIRM -> {
                    hideDialog()
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

    private fun initBaseDialog() {
        loadingDialog = if (showSystemProgress()) {
            ProgressDialog(mContext)
        } else {
            CustomProgressDialog(mContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        loadingDialog.dismiss()
        _binding = null
    }

    /*=======================================抽象方法==============================================*/
    abstract fun initView()
    /*=======================================重写方法==============================================*/

    override fun useEventBus(): Boolean {
        return false
    }

    /**
     * 是否显示系统进度条控件，默认为false，显示自定义菊花转
     */
    override fun showSystemProgress(): Boolean {
        return false
    }
    /*=======================================UI方法==============================================*/

    /**
     * 显示进度框
     */
    open fun showLoading(msg: String?, cancelEnable: Boolean) {
        loadingDialog.setCancelable(cancelEnable)
        if (loadingDialog is ProgressDialog) {
            (loadingDialog as ProgressDialog).setMessage(msg ?: "")
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
        AlertDialog.Builder(mContext).setTitle(getString(R.string.default_dialog_title))
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
        AlertDialog.Builder(mContext).setTitle(getString(R.string.default_dialog_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm), listener)
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
    @SuppressLint("ShowToast")
    open fun showToast(message: CharSequence?) {
        message?.let {
            ToastUtils.showShort(it)
        }
    }

    /**
     * 显示SnackBar
     */
    open fun makeSnackBar(view: View, message: CharSequence) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}