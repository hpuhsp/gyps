package com.swallow.fly.utils

import android.text.TextUtils
import com.swallow.fly.ext.toDateString
import com.swallow.fly.ext.toTimestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.Locale

/**
 * 日期工具类（旧版，已废弃）
 *
 * 旧方法存在线程安全问题（SimpleDateFormat 非线程安全），已全部标记 @Deprecated。
 * 请迁移到 com.swallow.fly.ext.DateExt 中的扩展函数。
 *
 * @Author: Hsp
 * @CreateTime: 2020/8/31
 */
@Deprecated("请迁移到 com.swallow.fly.ext.DateExt 中的扩展函数")
object DateUtils {

    const val PATTERN_E = "yyyy-MM-dd"

    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    private const val CHINA_DATE_FORMAT = "yyyy年MM月dd日"
    private const val DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DEFAULT_YEAR_MONTH = "yyyy-MM"
    private const val PATTERN_N = "HH:mm:ss"

    private val sdfCache = object : ThreadLocal<HashMap<String, SimpleDateFormat>>() {
        override fun initialValue() = HashMap<String, SimpleDateFormat>()
    }

    private fun getSdf(pattern: String, locale: Locale = Locale.getDefault()): SimpleDateFormat =
        sdfCache.get()!!.getOrPut("$pattern|${locale.language}") {
            SimpleDateFormat(pattern, locale)
        }

    @Deprecated("使用 String.toTimestamp()", ReplaceWith("dateStr.toTimestamp()", "com.swallow.fly.ext.toTimestamp"))
    fun dateToStamp(dateStr: String): Long {
        return try {
            getSdf(DEFAULT_DATE_FORMAT).parse(dateStr)?.time ?: Date().time
        } catch (e: ParseException) {
            e.printStackTrace()
            Date().time
        }
    }

    @Deprecated("使用 Long.toDateString()", ReplaceWith("timeMill.toDateString()", "com.swallow.fly.ext.toDateString"))
    fun getDateFromStamp(timeMill: Long): String =
        getSdf(DEFAULT_DATE_FORMAT, Locale.CHINA).format(Date(timeMill))

    @Deprecated("使用 Long.toDateString() 并传入 yyyy年MM月dd日 格式")
    fun getCnDateFromStamp(timeMill: Long): String =
        getSdf(CHINA_DATE_FORMAT, Locale.CHINA).format(Date(timeMill))

    @Deprecated("使用 Long.toDateString() 并传入 yyyy-MM-dd HH:mm:ss 格式")
    fun getDateMillisFromStamp(timeMill: Long): String =
        getSdf(DEFAULT_DATETIME_FORMAT, Locale.CHINA).format(Date(timeMill))

    @Deprecated("使用 Calendar 直接操作")
    fun isMonthLastDay(timer: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timer)
        calendar[Calendar.DATE] = calendar[Calendar.DATE] + 1
        return calendar[Calendar.DAY_OF_MONTH] == 1
    }

    @Deprecated("使用 System.currentTimeMillis().toDateString() 并传入 yyyy-MM 格式")
    fun getCurrentMonth(): String = getSdf(DEFAULT_YEAR_MONTH).format(Date())

    @Deprecated("使用 System.currentTimeMillis().toDateString()", ReplaceWith("System.currentTimeMillis().toDateString()", "com.swallow.fly.ext.toDateString"))
    fun getCurrentDate(): String = getSdf(PATTERN_E).format(Date())

    @Deprecated("使用 System.currentTimeMillis().toDateString() 并传入 HH:mm:ss 格式")
    fun getCurrentTime(): String = getSdf(PATTERN_N).format(Date())

    @Deprecated("使用 System.currentTimeMillis().toDateString() 并传入 yyyy-MM-dd HH:mm:ss 格式")
    fun getCurrentTime2(): String = getSdf(DEFAULT_DATETIME_FORMAT).format(Date())

    @Deprecated("使用 String.toTimestamp()", ReplaceWith("time.toTimestamp()", "com.swallow.fly.ext.toTimestamp"))
    fun parseDate(time: String?, pattern: String?): Long {
        return try {
            val fmt = pattern ?: DEFAULT_DATE_FORMAT
            if (TextUtils.isEmpty(time)) {
                System.currentTimeMillis()
            } else {
                getSdf(fmt).parse(time!!)?.time ?: System.currentTimeMillis()
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            System.currentTimeMillis()
        }
    }

    @Deprecated("直接比较 Date.time 与 System.currentTimeMillis()")
    fun isTrueDate(inputDate: Date): Boolean = inputDate.time <= System.currentTimeMillis()

    @Deprecated("使用 Long.toDateString() 并传入 yyyy-MM-dd HH:mm:ss 格式")
    fun timeStamp2Date(seconds: String?, format: String?): String {
        if (seconds == null || seconds.isEmpty() || seconds == "null") return ""
        val fmt = if (format.isNullOrEmpty()) DEFAULT_DATETIME_FORMAT else format
        return getSdf(fmt).format(Date(seconds.toLong() * 1000))
    }

    @Deprecated("使用 Long.toDateString()", ReplaceWith("timeMill.toDateString()", "com.swallow.fly.ext.toDateString"))
    fun getDateFromStampE(timeMill: Long): String =
        getSdf(PATTERN_E, Locale.CHINA).format(Date(timeMill))

    @Deprecated("使用 Long.toDateString()")
    fun getDateFormat(date: Date?, pattern: String?): String =
        getSdf(pattern ?: DEFAULT_DATE_FORMAT).format(date ?: Date())

    @Deprecated("使用 Long.toDateString()", ReplaceWith("System.currentTimeMillis().toDateString()", "com.swallow.fly.ext.toDateString"))
    val firstDayOfNextMonth: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, 1)
            calendar[Calendar.DAY_OF_MONTH] = 1
            return getDateFormat(calendar.time, DEFAULT_DATE_FORMAT)
        }

    @Deprecated("使用 Long.toDateString()", ReplaceWith("System.currentTimeMillis().toDateString()", "com.swallow.fly.ext.toDateString"))
    fun getFirstDayofNextMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        calendar[Calendar.DAY_OF_MONTH] = 1
        return getDateFormat(calendar.time, DEFAULT_DATE_FORMAT)
    }
}