package com.hnsh.core.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TextView
import java.util.*

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/31 10:40
 * @UpdateRemark:   更新说明：
 */
class DialogUtils {

    companion object {
        /**
         * 显示时间选择框
         */
        fun showDatePickerDialog(
            context: Context,
            textView: TextView,
            callBack: OnDateSelectCallBack
        ) {
            val calendar = GregorianCalendar()
            if (textView.tag != null) {
                calendar.timeInMillis = textView.tag as Long
            }
            val year = calendar[GregorianCalendar.YEAR]
            val month = calendar[GregorianCalendar.MONTH]
            val day = calendar[GregorianCalendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = month
                    calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    calendar[Calendar.HOUR_OF_DAY] = 0
                    calendar[Calendar.MINUTE] = 0
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0

                    textView.text = DateUtils.getDateFromStamp(calendar.timeInMillis)
                    textView.tag = calendar.timeInMillis
                    callBack.selectDate(calendar.timeInMillis)
                },
                year,
                month,
                day
            )
            // 设置最大和最小日期
            val datePicker: DatePicker = datePickerDialog.datePicker
            // TODO 根据数据库表jp_pos_sys_para_setup中的workDateIsLimit参数判断
            if (DateUtils.isMonthLastDay(System.currentTimeMillis())) {
                val minCalendar = Calendar.getInstance()
                minCalendar.time = Date(System.currentTimeMillis())
                minCalendar[Calendar.DATE] = calendar[Calendar.DATE] + 1
                datePicker.minDate = minCalendar.timeInMillis
            } else {
                datePicker.maxDate = Date().time
            }
            datePickerDialog.show()
        }


        /**
         * 显示时间选择框
         */
        fun showNormalDatePickerDialog(
            context: Context,
            textView: TextView,
            callBack: OnDateSelectCallBack
        ) {
            val calendar = GregorianCalendar()
            if (textView.tag != null) {
                calendar.timeInMillis = textView.tag as Long
            }
            val year = calendar[GregorianCalendar.YEAR]
            val month = calendar[GregorianCalendar.MONTH]
            val day = calendar[GregorianCalendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = month
                    calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    calendar[Calendar.HOUR_OF_DAY] = 0
                    calendar[Calendar.MINUTE] = 0
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0

                    callBack.selectDate(calendar.timeInMillis)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }
    }

    interface OnDateSelectCallBack {
        fun selectDate(stamp: Long)
    }
}