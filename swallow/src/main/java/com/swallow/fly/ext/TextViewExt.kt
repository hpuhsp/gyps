package com.swallow.fly.ext

import android.widget.TextView

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/21 10:42
 * @UpdateRemark:   更新说明：
 */
fun TextView.txt(): String {
    return if (this.text.isNullOrEmpty()) {
        ""
    } else {
        this.text.toString().trim()
    }
}