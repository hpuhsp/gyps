package com.hnsh.core.http

import androidx.annotation.NonNull
import okhttp3.HttpUrl

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 15:56
 * @UpdateRemark:   更新说明：
 */
interface BaseUrl {
    /**
     * 在调用 Retrofit API 接口之前,使用 Okhttp 或其他方式,请求到正确的 BaseUrl 并通过此方法返回
     *
     * @return
     */
    @NonNull
    fun url(): HttpUrl?

}