package com.swallow.fly.ext

import java.security.MessageDigest

/**
 * String 相关扩展函数
 *
 * 替代 FastUtils 中的字符串工具方法
 */

/** MD5 编码 */
fun String.toMD5(): String {
    return try {
        val hash = MessageDigest.getInstance("MD5").digest(toByteArray(Charsets.UTF_8))
        buildString(hash.size * 2) {
            for (b in hash) {
                val value = b.toInt() and 0xFF
                if (value < 0x10) append("0")
                append(Integer.toHexString(value))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
