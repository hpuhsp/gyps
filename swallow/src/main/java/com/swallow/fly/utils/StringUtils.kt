package com.swallow.fly.utils

import androidx.annotation.NonNull
import java.util.regex.Pattern

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/2 16:45
 * @UpdateRemark:   更新说明：
 */
object StringUtils {
    /**
     * 校验密码，至少包含大小写字母、数字及特殊符号的两种
     */
    fun passwordAvailable(@NonNull str: String): Boolean {
        val pattern =
            Pattern.compile("^(?![A-Z]+\$)(?![a-z]+\$)(?!\\d+\$)(?![\\W_]+\$)\\S{8,16}\$")
        return pattern.matcher(str).matches()
    }

    /**
     * 校验IPV4/IPV6 地址是否正确
     */
    fun checkIpAddress(@NonNull str: String): Boolean {
        val pattern =
            Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\$")
        return pattern.matcher(str).matches()
    }
}