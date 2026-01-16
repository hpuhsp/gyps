package com.swallow.fly.ext.navigation

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Bundle 扩展函数
 * 提供便捷的参数构建和获取
 */

/**
 * 创建 Bundle 的 DSL 方式
 */
inline fun bundleOf(builder: Bundle.() -> Unit): Bundle {
    return Bundle().apply(builder)
}

/**
 * 快速创建 Bundle
 */
fun bundleOf(vararg pairs: Pair<String, Any?>): Bundle {
    return Bundle().apply {
        pairs.forEach { (key, value) ->
            when (value) {
                null -> putString(key, null)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Double -> putDouble(key, value)
                is Boolean -> putBoolean(key, value)
                is Char -> putChar(key, value)
                is Byte -> putByte(key, value)
                is Short -> putShort(key, value)
                is CharSequence -> putCharSequence(key, value)
                is Parcelable -> putParcelable(key, value)
                is Serializable -> putSerializable(key, value)
                is IntArray -> putIntArray(key, value)
                is LongArray -> putLongArray(key, value)
                is FloatArray -> putFloatArray(key, value)
                is DoubleArray -> putDoubleArray(key, value)
                is BooleanArray -> putBooleanArray(key, value)
                is CharArray -> putCharArray(key, value)
                is ByteArray -> putByteArray(key, value)
                is ShortArray -> putShortArray(key, value)
                is Bundle -> putBundle(key, value)
                else -> throw IllegalArgumentException("Unsupported type: ${value::class.java}")
            }
        }
    }
}

/**
 * 安全获取 String
 */
fun Bundle.getStringSafe(key: String, defaultValue: String = ""): String {
    return getString(key) ?: defaultValue
}

/**
 * 安全获取 Int
 */
fun Bundle.getIntSafe(key: String, defaultValue: Int = 0): Int {
    return getInt(key, defaultValue)
}

/**
 * 安全获取 Long
 */
fun Bundle.getLongSafe(key: String, defaultValue: Long = 0L): Long {
    return getLong(key, defaultValue)
}

/**
 * 安全获取 Boolean
 */
fun Bundle.getBooleanSafe(key: String, defaultValue: Boolean = false): Boolean {
    return getBoolean(key, defaultValue)
}

/**
 * 安全获取 Float
 */
fun Bundle.getFloatSafe(key: String, defaultValue: Float = 0f): Float {
    return getFloat(key, defaultValue)
}

/**
 * 安全获取 Double
 */
fun Bundle.getDoubleSafe(key: String, defaultValue: Double = 0.0): Double {
    return getDouble(key, defaultValue)
}

/**
 * 安全获取 Parcelable
 */
inline fun <reified T : Parcelable> Bundle.getParcelableSafe(key: String): T? {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelable(key)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 安全获取 Serializable
 */
inline fun <reified T : Serializable> Bundle.getSerializableSafe(key: String): T? {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getSerializable(key) as? T
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
