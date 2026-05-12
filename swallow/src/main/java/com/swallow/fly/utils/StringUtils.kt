package com.swallow.fly.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * 字符串工具类
 *
 * 提供字符串验证、格式化等功能
 *
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/2 16:45
 * @UpdateRemark:   2026-03-13 新增扩展函数，提供更多字符串处理功能
 */
object StringUtils {
    /**
     * 校验密码，至少包含大小写字母、数字及特殊符号的两种
     */
    fun passwordAvailable(str: String): Boolean {
        val pattern =
            Pattern.compile("^(?![A-Z]+\$)(?![a-z]+\$)(?!\\d+\$)(?![\\W_]+\$)\\S{8,16}\$")
        return pattern.matcher(str).matches()
    }

    /**
     * 校验IPV4/IPV6 地址是否正确
     */
    fun checkIpAddress(str: String): Boolean {
        val pattern = Pattern.compile(
            "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\$"
        )
        return pattern.matcher(str).matches()
    }

    // ========== 新增扩展函数（推荐使用） ==========

    /**
     * 验证邮箱格式
     *
     * @return true 表示格式正确
     *
     * 示例：
     * ```kotlin
     * if (email.isValidEmail()) {
     *     // 邮箱格式正确
     * }
     * ```
     */
    fun String.isValidEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    /**
     * 验证手机号格式
     *
     * @return true 表示格式正确
     *
     * 示例：
     * ```kotlin
     * if (phone.isValidPhone()) {
     *     // 手机号格式正确
     * }
     * ```
     */
    fun String.isValidPhone(): Boolean {
        return Patterns.PHONE.matcher(this).matches()
    }

    /**
     * 验证 URL 格式
     *
     * @return true 表示格式正确
     *
     * 示例：
     * ```kotlin
     * if (url.isValidUrl()) {
     *     // URL 格式正确
     * }
     * ```
     */
    fun String.isValidUrl(): Boolean {
        return Patterns.WEB_URL.matcher(this).matches()
    }

    /**
     * 验证中国大陆手机号（严格模式）
     *
     * 11位数字，以1开头
     *
     * @return true 表示格式正确
     *
     * 示例：
     * ```kotlin
     * if (phone.isValidChinaPhone()) {
     *     // 中国手机号格式正确
     * }
     * ```
     */
    fun String.isValidChinaPhone(): Boolean {
        val pattern = Pattern.compile("^1[3-9]\\d{9}\$")
        return pattern.matcher(this).matches()
    }

    /**
     * 验证身份证号（18位）
     *
     * @return true 表示格式正确
     *
     * 示例：
     * ```kotlin
     * if (idCard.isValidIdCard()) {
     *     // 身份证号格式正确
     * }
     * ```
     */
    fun String.isValidIdCard(): Boolean {
        val pattern = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))" +
                    "(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]\$"
        )
        return pattern.matcher(this).matches()
    }

    /**
     * 截断字符串
     * 
     * @param maxLength 最大长度
     * @param suffix 后缀，默认 "..."
     * @return 截断后的字符串
     * 
     * 示例：
     * ```kotlin
     * val short = longText.truncate(20)
     * val short = longText.truncate(20, "…")
     * ```
     */
    fun String.truncate(maxLength: Int, suffix: String = "..."): String {
        return if (length <= maxLength) this
        else substring(0, maxLength - suffix.length) + suffix
    }

    /**
     * 手机号脱敏
     * 
     * 将中间4位替换为 ****
     * 
     * @return 脱敏后的手机号
     * 
     * 示例：
     * ```kotlin
     * val masked = "13812345678".maskPhone()
     * // 输出：138****5678
     * ```
     */
    fun String.maskPhone(): String {
        return if (length >= 11) {
            substring(0, 3) + "****" + substring(7)
        } else {
            this
        }
    }

    /**
     * 邮箱脱敏
     * 
     * 将 @ 前面的部分部分隐藏
     * 
     * @return 脱敏后的邮箱
     * 
     * 示例：
     * ```kotlin
     * val masked = "example@gmail.com".maskEmail()
     * // 输出：ex***@gmail.com
     * ```
     */
    fun String.maskEmail(): String {
        val atIndex = indexOf("@")
        if (atIndex <= 0) return this
        
        val username = substring(0, atIndex)
        val domain = substring(atIndex)
        
        return when {
            username.length <= 2 -> "${username[0]}***$domain"
            username.length <= 4 -> "${username.substring(0, 2)}***$domain"
            else -> "${username.substring(0, 3)}***$domain"
        }
    }

    /**
     * 身份证号脱敏
     * 
     * 将中间部分替换为 ****
     * 
     * @return 脱敏后的身份证号
     * 
     * 示例：
     * ```kotlin
     * val masked = "110101199001011234".maskIdCard()
     * // 输出：110101********1234
     * ```
     */
    fun String.maskIdCard(): String {
        return if (length == 18) {
            substring(0, 6) + "********" + substring(14)
        } else {
            this
        }
    }

    /**
     * 判断字符串是否为空或空白
     *
     * @return true 表示为空或只包含空白字符
     *
     * 示例：
     * ```kotlin
     * if (text.isNullOrEmpty()) {
     *     // 字符串为空或空白
     * }
     * ```
     */
    fun String?.isNullOrBlankSafe(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    /**
     * 判断字符串是否不为空且不为空白
     *
     * @return true 表示不为空且包含非空白字符
     *
     * 示例：
     * ```kotlin
     * if (text.isNotBlankSafe()) {
     *     // 字符串有内容
     * }
     * ```
     */
    fun String?.isNotBlankSafe(): Boolean {
        return this != null && this.trim().isNotEmpty()
    }

    /**
     * 移除所有空白字符
     * 
     * @return 移除空白后的字符串
     * 
     * 示例：
     * ```kotlin
     * val clean = "  hello  world  ".removeAllWhitespace()
     * // 输出：helloworld
     * ```
     */
    fun String.removeAllWhitespace(): String {
        return replace("\\s+".toRegex(), "")
    }

    /**
     * 首字母大写
     * 
     * @return 首字母大写的字符串
     * 
     * 示例：
     * ```kotlin
     * val capitalized = "hello".capitalize()
     * // 输出：Hello
     * ```
     */
    fun String.capitalizeFirst(): String {
        return if (isEmpty()) this
        else this[0].uppercase() + substring(1)
    }

    /**
     * 首字母小写
     * 
     * @return 首字母小写的字符串
     * 
     * 示例：
     * ```kotlin
     * val decapitalized = "Hello".decapitalizeFirst()
     * // 输出：hello
     * ```
     */
    fun String.decapitalizeFirst(): String {
        return if (isEmpty()) this
        else this[0].lowercase() + substring(1)
    }

    /**
     * 反转字符串
     * 
     * @return 反转后的字符串
     * 
     * 示例：
     * ```kotlin
     * val reversed = "hello".reverse()
     * // 输出：olleh
     * ```
     */
    fun String.reverse(): String {
        return reversed()
    }

    /**
     * 判断字符串是否只包含数字
     * 
     * @return true 表示只包含数字
     * 
     * 示例：
     * ```kotlin
     * if (text.isDigitsOnly()) {
     *     // 只包含数字
     * }
     * ```
     */
    fun String.isDigitsOnly(): Boolean {
        return all { it.isDigit() }
    }

    /**
     * 判断字符串是否只包含字母
     * 
     * @return true 表示只包含字母
     * 
     * 示例：
     * ```kotlin
     * if (text.isLettersOnly()) {
     *     // 只包含字母
     * }
     * ```
     */
    fun String.isLettersOnly(): Boolean {
        return all { it.isLetter() }
    }

    /**
     * 判断字符串是否只包含字母和数字
     * 
     * @return true 表示只包含字母和数字
     * 
     * 示例：
     * ```kotlin
     * if (text.isAlphanumeric()) {
     *     // 只包含字母和数字
     * }
     * ```
     */
    fun String.isAlphanumeric(): Boolean {
        return all { it.isLetterOrDigit() }
    }
}