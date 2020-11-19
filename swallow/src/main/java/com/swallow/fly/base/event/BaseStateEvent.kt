package com.swallow.fly.base.event

import android.os.Bundle

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/26 19:08
 * @UpdateRemark:   更新说明：
 */
data class BaseStateEvent(
    var event: EventArgs = EventArgs.DO_NOTHING,
    var code: Int = -1,
    var message: Int = 0,
    var content: String = "",
    var errorMsg: String? = null,
    var bundle: Bundle? = null,
    var toastMsg: String = ""
)

enum class EventArgs {
    DO_NOTHING,
    SHOW_LOADING,
    SHOW_CONFIRM,
    HIDE_DIALOG,
    SHOW_ERROR,
    SHOW_TOAST,
    KILL_APP
}