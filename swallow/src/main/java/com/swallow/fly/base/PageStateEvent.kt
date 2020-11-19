package com.swallow.fly.base

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/25 14:33
 * @UpdateRemark:   更新说明：
 */
data class PageStateEvent(
    val showLoading: Boolean = false,
    val loadingMsg: String? = null,
    val showEmpty: Boolean=false,
    val showError: Boolean=false,
    val errorCode: Int=-1,
    val errorMsg: String? = null
)
