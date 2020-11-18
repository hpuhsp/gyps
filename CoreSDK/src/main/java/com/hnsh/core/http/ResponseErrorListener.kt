package com.hnsh.core.http

/**
 * @Description: 网络请求错误监听
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/12 17:34
 * @UpdateRemark:   更新说明：
 */
interface ResponseErrorListener {
    fun handleResponseError(
        t: Throwable?
    ): Throwable?
}