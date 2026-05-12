package com.swallow.fly.ext

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.Locale

/**
 * 日期/时间扩展函数（线程安全）
 *
 * API 26+ 使用 java.time（不可变，天然线程安全）
 * API 24-25 使用 ThreadLocal<SimpleDateFormat>（每线程独立实例）
 *
 * 替代 DateUtils 中的旧方法
 */

private const val FMT_DATE = "yyyy-MM-dd"
private const val FMT_TIME = "HH:mm"

// ThreadLocal 缓存，避免旧版本每次 new SimpleDateFormat
private val sdfCache = object : ThreadLocal<HashMap<String, SimpleDateFormat>>() {
    override fun initialValue() = HashMap<String, SimpleDateFormat>()
}

private fun getSdf(pattern: String): SimpleDateFormat =
    sdfCache.get()!!.getOrPut(pattern) { SimpleDateFormat(pattern, Locale.getDefault()) }

// ─── 时间戳 → 字符串 ───────────────────────────────────────────────────────────

/**
 * 时间戳转日期字符串
 *
 * ```kotlin
 * System.currentTimeMillis().toDateString()               // "2024-01-01"
 * timestamp.toDateString("yyyy-MM-dd HH:mm:ss")           // "2024-01-01 12:30:00"
 * ```
 */
fun Long.toDateString(pattern: String = FMT_DATE): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(pattern))
    } else {
        getSdf(pattern).format(Date(this))
    }
}

// ─── 字符串 → 时间戳 ───────────────────────────────────────────────────────────

/**
 * 日期字符串转时间戳（毫秒）
 *
 * ```kotlin
 * "2024-01-01".toTimestamp()
 * "2024-01-01 12:30:00".toTimestamp("yyyy-MM-dd HH:mm:ss")
 * ```
 */
fun String.toTimestamp(pattern: String = FMT_DATE): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        runCatching {
            LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrDefault(System.currentTimeMillis())
    } else {
        runCatching {
            getSdf(pattern).parse(this)?.time ?: System.currentTimeMillis()
        }.getOrDefault(System.currentTimeMillis())
    }
}

// ─── 时间判断 ──────────────────────────────────────────────────────────────────

/** 是否是今天 */
fun Long.isToday(): Boolean = isSameDay(this, System.currentTimeMillis())

/** 是否是昨天 */
fun Long.isYesterday(): Boolean {
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
    return isSameDay(this, yesterday.timeInMillis)
}

/** 是否是本周 */
fun Long.isThisWeek(): Boolean {
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = this@isThisWeek }
    return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
        now.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)
}

/** 是否是本月 */
fun Long.isThisMonth(): Boolean {
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = this@isThisMonth }
    return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
        now.get(Calendar.MONTH) == date.get(Calendar.MONTH)
}

/** 是否是今年 */
fun Long.isThisYear(): Boolean {
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = this@isThisYear }
    return now.get(Calendar.YEAR) == date.get(Calendar.YEAR)
}

// ─── 友好时间描述 ──────────────────────────────────────────────────────────────

/**
 * 友好时间描述
 *
 * - 1分钟内 → 刚刚
 * - 1小时内 → X分钟前
 * - 今天    → 今天 HH:mm
 * - 昨天    → 昨天 HH:mm
 * - 今年    → MM-dd HH:mm
 * - 往年    → yyyy-MM-dd HH:mm
 */
fun Long.toFriendlyTime(): String {
    val diff = System.currentTimeMillis() - this
    return when {
        diff < 60_000L -> "刚刚"
        diff < 3_600_000L -> "${diff / 60_000}分钟前"
        isToday() -> "今天 ${toDateString(FMT_TIME)}"
        isYesterday() -> "昨天 ${toDateString(FMT_TIME)}"
        isThisYear() -> toDateString("MM-dd HH:mm")
        else -> toDateString("yyyy-MM-dd HH:mm")
    }
}

// ─── 内部工具 ──────────────────────────────────────────────────────────────────

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply {
        timeInMillis = t1
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val c2 = Calendar.getInstance().apply {
        timeInMillis = t2
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return c1.timeInMillis == c2.timeInMillis
}
