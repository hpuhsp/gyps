package com.hsp.resource.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.text.InputType
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.hsp.resource.R
import com.swallow.fly.utils.FastUtils

/**
 * @Description: 编辑输入框
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/9/26 9:20
 * @UpdateRemark:
 */
class CustomEditDialog(private val mContext: Context) :
    Dialog(mContext, R.style.sku_dialog), OnShowListener, View.OnClickListener {

    var tvTitle: TextView? = null
    var tvSubTitle: TextView? = null
    var etContent: EditText? = null
    var btnCancel: TextView? = null
    var btnConfirm: TextView? = null
    var tvExtraInfo: TextView? = null
    private var mOnConfirmClickListener: OnConfirmClickListener? =
        null

    /**
     * 初始化自定义Dialog
     */
    private fun initDialog() {
        val contentView: View =
            LayoutInflater.from(mContext).inflate(R.layout.dialog_custom_edit_layout, null)
        this.setContentView(contentView)
        tvTitle = contentView.findViewById(R.id.tv_title)
        tvSubTitle = contentView.findViewById(R.id.tv_sub_title)
        etContent = contentView.findViewById(R.id.et_content)
        btnCancel = contentView.findViewById(R.id.btn_cancel)
        btnConfirm = contentView.findViewById(R.id.btn_confirm)
        tvExtraInfo = contentView.findViewById(R.id.tv_extra_info)


        val window = window
        if (null != window) {
            // 屏幕的72%宽度
            val dialogWidth = (FastUtils.getScreenWidth(mContext) * 0.72).toInt()
            window.setGravity(Gravity.CENTER)
            window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }

    private fun initView(builder: Builder?) {
        if (null != builder) {
            if (!builder.title.isNullOrEmpty()) {
                tvTitle?.text = builder.title
            } else {
                tvTitle?.text = ""
            }
            if (!TextUtils.isEmpty(builder.subTitle)) {
                tvSubTitle?.text = builder.subTitle
            } else {
                tvSubTitle?.text = ""
            }
            if (!TextUtils.isEmpty(builder.extraInfo)) {
                tvExtraInfo?.visibility = View.VISIBLE
                tvExtraInfo?.text = builder.extraInfo
            } else {
                tvExtraInfo?.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(builder.contentHint)) {
                etContent?.hint = builder.contentHint
            } else {
                etContent?.hint = ""
            }
            if (!TextUtils.isEmpty(builder.content)) {
                etContent?.setText(builder.content)
                etContent?.setSelection(builder.content!!.length)
            }
            etContent?.inputType = builder.etInputType
            setOnShowListener(this)
        }
    }

    fun setContent(content: String) {
        if (!TextUtils.isEmpty(content)) {
            etContent!!.setText(content)
            etContent!!.setSelection(content.length)
        }
    }

    override fun show() {
        super.show()
        requestInputFocus()
    }

    /**
     * 强制输入框获取焦点
     */
    fun requestInputFocus() {
        if (null != etContent) {
            etContent?.requestFocus()
            etContent?.isCursorVisible = true
        }
    }

    /**
     * 校验输入数据合法性
     */
    private fun checkInputText() {
        FastUtils.collapseSoftInputMethod(mContext, etContent)
        if (etContent?.text.isNullOrEmpty()) {
            FastUtils.makeText(
                mContext, "您还没有输入内容！"
            )
            return
        }
        if (null != mOnConfirmClickListener) {
            mOnConfirmClickListener?.onDialogInputText(etContent?.text?.toString() ?: "")
        }
        dismiss()
    }

    fun addOnConfirmClickListener(listener: OnConfirmClickListener?) {
        mOnConfirmClickListener = listener
    }

    fun setContentMessage(contentMessage: String) {
        etContent!!.setText(contentMessage)
        etContent!!.setSelection(contentMessage.length)
    }

    override fun onShow(dialog: DialogInterface) {
        if (null != etContent) {
            etContent!!.postDelayed({
                val imm =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(
                    etContent,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }, 200)
        }
    }

    interface OnConfirmClickListener {
        fun onDialogInputText(text: String)
    }

    class Builder(context: Context) {
        var title: String? = null
        var subTitle: String? = null
        var extraInfo: String? = null
        var contentHint: String? = null
        var content: String? = null
        private val customEditDialog: CustomEditDialog = CustomEditDialog(context)
        var etInputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        fun setTitle(text: String?): Builder {
            title = text
            return this
        }

        fun setSubTitle(text: String?): Builder {
            subTitle = text
            return this
        }

        fun setExtraInfo(extraInfo: String?): Builder {
            this.extraInfo = extraInfo
            return this
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setEditHint(hint: String?): Builder {
            contentHint = hint
            return this
        }

        fun setInputType(inputType: Int): Builder {
            etInputType = inputType
            return this
        }

        fun create(): CustomEditDialog {
            customEditDialog.initView(this)
            return customEditDialog
        }

    }

    init {
        initDialog()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_confirm -> checkInputText()
            else -> {
            }
        }
    }
}