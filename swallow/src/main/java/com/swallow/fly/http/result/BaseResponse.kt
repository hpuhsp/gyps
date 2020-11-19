package com.swallow.fly.http.result

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/22 10:54
 * @UpdateRemark:   更新说明：
 */
class BaseResponse<T>(
    var code: Int = -1,
    var status: String,
    var message: String,
    var data: T,
    var token: String? = ""
) {
    fun isSuccessful(): Boolean {
        return code == 0 || status == "success"
    }
}