package com.swallow.fly.base.ui.delegate

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.swallow.fly.R
import com.swallow.fly.widget.CustomProgressDialog

/**
 * 进度弹窗代理接口
 */
interface ProgressDelegate {
    fun showLoading(context: Context, msg: String? = null, cancelEnable: Boolean = false)
    fun hideDialog()
    fun showConfirmDialog(context: Context, message: String?, cancelEnable: Boolean)
    fun showConfirmDialog(
        context: Context,
        message: String?,
        listener: DialogInterface.OnClickListener
    )

    fun showTipsDialog(context: Context, message: String?, cancelEnable: Boolean)
}

/**
 * 进度弹窗默认实现
 */
class ProgressDelegateImpl : ProgressDelegate {

    private var currentDialog: CustomProgressDialog? = null

    override fun showLoading(context: Context, msg: String?, cancelEnable: Boolean) {
        hideDialog() // 互斥

        if (currentDialog == null) {
            currentDialog = CustomProgressDialog(context)
        }

        currentDialog?.apply {
            setMessage(msg)
            setCancelable(cancelEnable)
            setCanceledOnTouchOutside(cancelEnable)
            if (!isShowing) show()
        }
    }

    override fun hideDialog() {
        if (currentDialog?.isShowing == true) {
            currentDialog?.dismiss()
        }
        currentDialog = null
    }

    // Confirm Dialogs use MaterialAlertDialogBuilder directly as they are standard alerts
    override fun showConfirmDialog(context: Context, message: String?, cancelEnable: Boolean) {
        if (message.isNullOrEmpty()) return
        hideDialog()
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.default_dialog_title))
            .setMessage(message)
            .setCancelable(cancelEnable)
            .setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun showConfirmDialog(
        context: Context,
        message: String?,
        listener: DialogInterface.OnClickListener
    ) {
        if (message.isNullOrEmpty()) return
        hideDialog()
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.default_dialog_title))
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.confirm), listener)
            .show()
    }

    override fun showTipsDialog(context: Context, message: String?, cancelEnable: Boolean) {
        if (message.isNullOrEmpty()) return
        hideDialog()
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.default_dialog_title))
            .setMessage(message)
            .setCancelable(cancelEnable)
            .setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                dialog?.dismiss()
            }
            .show()
    }
}
