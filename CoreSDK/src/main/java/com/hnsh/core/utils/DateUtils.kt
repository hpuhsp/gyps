package com.hnsh.core.utils

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/31 15:07
 * @UpdateRemark:   更新说明：
 */
object DateUtils {
    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    private const val CHINA_DATE_FORMAT = "yyyy年MM月dd日"
    private const val DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DEFAULT_YEAR_MONTH = "yyyy-MM"
    const val PATTERN_E = "yyyy-MM-dd"
    private const val PATTERN_N = "HH:mm:ss"
    /**
     * 日期字符串转时间戳
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    fun dateToStamp(dateStr: String): Long {
        val format = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
        try {
            return format.parse(dateStr).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date().time
    }

    /**
     * YY/MM/dd
     *
     * @param timeMill 毫秒单位
     * @return
     */
    fun getDateFromStamp(timeMill: Long): String {
        val d = Date(timeMill)
        val format = SimpleDateFormat(
            DEFAULT_DATE_FORMAT, Locale.CHINA
        )
        return format.format(d)
    }

    /**
     * yyyy年MM月dd日
     *
     * @param timeMill 毫秒单位
     * @return
     */
    fun getCnDateFromStamp(timeMill: Long): String {
        val d = Date(timeMill)
        val format = SimpleDateFormat(
            CHINA_DATE_FORMAT, Locale.CHINA
        )
        return format.format(d)
    }

    /**
     * YY-MM-dd
     *
     * @param timeMill 毫秒单位
     * @return
     */
    fun getDateMillisFromStamp(timeMill: Long): String {
        val d = Date(timeMill)
        val format = SimpleDateFormat(
            DEFAULT_DATETIME_FORMAT, Locale.CHINA
        )
        return format.format(d)
    }

    /**
     * 判断是否是当月最后一天
     */
    fun isMonthLastDay(timer: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timer)
        calendar[Calendar.DATE] = calendar[Calendar.DATE] + 1
        return calendar[Calendar.DAY_OF_MONTH] == 1
    }

    fun getCurrentMonth(): String {
        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat(DEFAULT_YEAR_MONTH)
        return sdf.format(now.timeInMillis)
    }

    fun getCurrentDate(): String? {
        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat(PATTERN_E)
        return sdf.format(now.timeInMillis)
    }

    fun getCurrentTime(): String? {
        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat(PATTERN_N)
        return sdf.format(now.timeInMillis)
    }

    fun getCurrentTime2():String?{
        val currentDate=Date()
        val sdf=SimpleDateFormat(DEFAULT_DATETIME_FORMAT)
        return sdf.format(currentDate)
    }

//    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss") val currentDate = sdf.format(Date()) System.out.println(" C DATE is "+currentDate)

    /**
     * 根据时间字符串  得到 时间戳
     * @param time
     * @param pattern 时间格式模式  [PATTERN_A：yyyy年MM月dd日 HH时mm分][PATTERN_B：yyyy年MM月dd日]
     * [PATTERN_C：yyyy年MM月dd日 HH:mm:ss][PATTERN_D：yyyy年MM月dd日 HH:mm][PATTERN_E：yyyy年MM月dd日]
     * [PATTERN_F：HH:mm][PATTERN_G：HH时mm分]
     * @return timeInMillis
     */
    fun parseDate(time: String?, pattern: String?): Long {
        return try {
            val simpleDateFormat = SimpleDateFormat(pattern)
            if (TextUtils.isEmpty(time)) {
                System.currentTimeMillis()
            } else simpleDateFormat.parse(time).time
        } catch (e: ParseException) {
            e.printStackTrace()
            System.currentTimeMillis()
        }
    }

    /**
     * 以当前时间为基准点  比较时间
     *
     * @param inputDate
     * @return 参数时间在当前时间之后 return false , 否则  return true
     */
    fun isTrueDate(inputDate: Date): Boolean {
        val instance = Calendar.getInstance()
        val currTime = instance.time
        return if (inputDate.time > currTime.time) {
            false
        } else {
            true
        }
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @return
     */
    fun timeStamp2Date(seconds: String?, format: String?): String {
        var format = format
        if (seconds == null || seconds.isEmpty() || seconds == "null") {
            return ""
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(java.lang.Long.valueOf(seconds + "000")))
    }//获取下个月开始日期

    /**
     * 获取下个月的开始日期
     *
     * @return yyyy-MM-dd  格式
     */
    val firstDayOfNextMonth: String
        get() {
            //获取下个月开始日期
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, 1)
            calendar[Calendar.DAY_OF_MONTH] = 1
            return getDateFormat(calendar.time, DEFAULT_DATE_FORMAT)
        }
    fun  getFirstDayofNextMonth():String {
        val  calendar=Calendar.getInstance()
        calendar.add(Calendar.MONTH,1)
        calendar[Calendar.DAY_OF_MONTH]=1
        return getDateFormat(calendar.time, DEFAULT_DATE_FORMAT)
    }
    /**
     * YY-MM-dd
     *
     * @param timeMill 毫秒单位
     * @return
     */
    fun getDateFromStampE(timeMill: Long): String {
        val d = Date(timeMill)
        val format = SimpleDateFormat(
            PATTERN_E, Locale.CHINA
        )
        return format.format(d)
    }

    /**
     * 获取指定时间 格式化
     *
     * @param date
     * @param pattern 时间格式模式  [PATTERN_A：yyyy年MM月dd日 HH时mm分][PATTERN_B：yyyy年MM月dd日]
     * [PATTERN_C：yyyy年MM月dd日 HH:mm:ss][PATTERN_D：yyyy年MM月dd日 HH:mm][PATTERN_E：yyyy年MM月dd日]
     * [PATTERN_F：HH:mm][PATTERN_G：HH时mm分]
     * @return
     */
    fun getDateFormat(date: Date?, pattern: String?): String {
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(date)
    }
}