package com.hsp.resource.widget

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.hsp.resource.R
import com.hsp.resource.dialog.CustomEditDialog
import com.swallow.fly.utils.FastUtils

/**
 * @Description: 自定义数量加减控件
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/26 9:03
 * @UpdateRemark:   更新说明：
 */
class NumberLiftView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    var cbReduce: CheckBox? = null
    var tvCount: TextView? = null
    var cbAdd: CheckBox? = null
    private var count = 0
    private var mOnNumberChangeListener: OnNumberChangeListener? = null
    private var customEditDialog: CustomEditDialog? = null
    private var minLimitValue = 0 // 限定最小值
    private var maxLimitValue = Int.MAX_VALUE // 限定最大值

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.NumberLiftView)
        minLimitValue = typedArray.getInt(R.styleable.NumberLiftView_lift_minLimit, 0)
        maxLimitValue =
            typedArray.getInt(R.styleable.NumberLiftView_lift_maxLimit, Int.MAX_VALUE)
        typedArray.recycle()
        orientation = HORIZONTAL
        val rootView = LayoutInflater.from(context).inflate(R.layout.number_lift_view_layout, this)
        cbReduce = rootView.findViewById(R.id.cb_reduce)
        tvCount = rootView.findViewById(R.id.tv_count)
        cbAdd = rootView.findViewById(R.id.cb_add)
        cbReduce?.setOnClickListener(this)
        tvCount?.setOnClickListener(this)
        cbAdd?.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cb_reduce -> {
                count--
                calculatingAction(false)
            }
            R.id.tv_count -> showInputDialog()
            R.id.cb_add -> {
                count++
                calculatingAction(true)
            }
            else -> {
            }
        }
    }

    /**
     * 显示数字键盘输入弹框
     */
    private fun showInputDialog() {
        if (null == customEditDialog) {
            return
        }
        customEditDialog?.setContentMessage(if (count == 0) "" else count.toString())
        customEditDialog?.show()
    }

    /**
     * 增加\减少
     */
    private fun calculatingAction(adder: Boolean) {
        // 处理最小值
        if (count < minLimitValue) {
            count = minLimitValue
            FastUtils.makeText(context, context.getString(R.string.reach_min_limit_value))
            return
        }
        if (count > maxLimitValue) {
            count = maxLimitValue
            FastUtils.makeText(context, context.getString(R.string.reach_max_limit_value))
            return
        }
        tvCount?.text = count.toString()
        if (null != mOnNumberChangeListener) {
            mOnNumberChangeListener!!.onCountChanged(count, adder)
        }
    }


    fun addOnNumberChangeListener(listener: OnNumberChangeListener?) {
        mOnNumberChangeListener = listener
    }

    fun setTextColor(color: Int) {
        if (null != tvCount) {
            tvCount!!.setTextColor(color)
        }
    }

    fun setCount(adjustCount: Int) {
        count = adjustCount
        if (null != tvCount) {
            tvCount!!.text = adjustCount.toString()
        }
    }

    /**
     * 设置弹框显示内容
     */
    fun setDialogConfig(
        title: String?,
        subTitle: String?,
        hint: String?,
        extraInfo: String?
    ) {
        if (null == customEditDialog) {
            customEditDialog = CustomEditDialog.Builder(context)
                .setTitle(title)
                .setSubTitle(subTitle)
                .setExtraInfo(extraInfo)
                .setEditHint(hint)
                .setInputType(if (minLimitValue >= 0) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .create()
            customEditDialog?.etContent?.filters =
                arrayOf<InputFilter>(InputFilterMinMax(minLimitValue, maxLimitValue, context))
            customEditDialog?.addOnConfirmClickListener(object :
                CustomEditDialog.OnConfirmClickListener {
                override fun onDialogInputText(text: String) {
                    val value = text.toInt()
                    val adder = count < value
                    count = value
                    calculatingAction(adder)
                }
            })
        }
    }

    class InputFilterMinMax : InputFilter {
        private var context: Context? = null
        private var min: Int
        private var max: Int

        constructor(min: Int, max: Int, context: Context) {
            this.min = min
            this.max = max
            this.context = context
        }

        constructor(min: String, max: String) {
            this.min = min.toInt()
            this.max = max.toInt()
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            try {
                if (source == "-") {
                    return ""
                }
                val input = (dest.toString() + source.toString()).toInt()
                if (isInRange(min, max, input)) {
                    return ""
                } else {
                    FastUtils.makeText(context, context?.getString(R.string.out_of_input_range))
                }
            } catch (nfe: NumberFormatException) {
            }
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    interface OnNumberChangeListener {
        fun onCountChanged(value: Int, isAdd: Boolean)
    }

    fun getMinLimitValue(): Int {
        return minLimitValue
    }

    fun setMinLimitValue(minLimitValue: Int) {
        this.minLimitValue = minLimitValue
    }

    fun setMaxLimitValue(maxLimitValue: Int) {
        this.maxLimitValue = maxLimitValue
    }

    fun getCustomEditDialog(): CustomEditDialog? {
        return customEditDialog
    }
}