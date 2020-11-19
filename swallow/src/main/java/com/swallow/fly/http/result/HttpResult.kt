package com.swallow.fly.http.result

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/24 10:08
 * @UpdateRemark:   更新说明：
 */
sealed class HttpResult<out T> {
    data class Success<out T>(val value: T) : HttpResult<T>()

    data class Failure(val throwable: Throwable?) :
        HttpResult<Nothing>()
}

inline fun <reified T> HttpResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is HttpResult.Success) {
        success(value)
    }
}

/**
 * 可调用进行自定义错误处理逻辑
 */
inline fun <reified T> HttpResult<T>.doFailure(
    failure: (Throwable?) -> Unit
) {
    if (this is HttpResult.Failure) {
        failure(throwable)
    }
}