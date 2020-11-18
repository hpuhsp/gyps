package com.hnsh.core.base.viewstate


/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/23 15:00
 * @UpdateRemark:   更新说明：
 */
data class PageListState<T>(
    var isRefresh: Boolean = true,
    var list: List<T> = ArrayList()
)