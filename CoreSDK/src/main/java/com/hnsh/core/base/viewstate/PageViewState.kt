package com.hnsh.core.base.viewstate

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/26 16:25
 * @UpdateRemark:   更新说明：
 */
data class PageViewState<T>(
    var data: T? = null,
    var code: Int = -1
)