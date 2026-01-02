package com.swallow.fly.http

import android.content.Context
import android.net.ParseException
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.swallow.fly.utils.FastUtils
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description: 自定义全局错误处理
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/12 17:37
 * @UpdateRemark:   更新说明：
 */
@Singleton
class ResponseErrorListenerImpl @Inject constructor(val context: Context) :
    ResponseErrorListener {

    /**
     * Handles response errors from network requests.
     *
     * This function takes a `Throwable` as input, determines the type of error,
     * and displays a user-friendly toast message. It also logs the error message for debugging purposes.
     * It can be extended to implement more specific logic for different error types.
     *
     * @param t The `Throwable` caught during the network request.
     * @return The original `Throwable` after handling.
     */
    override fun handleResponseError(t: Throwable?): Throwable? {
        if (null == t) {
            ToastUtils.showShort("未知错误!")
            return t
        }

        Timber.tag("Catch-Error").w(t?.message)
        var msg: String? = "未知错误"
        if (t is UnknownHostException) {
            msg = "网络不可用"
        } else if (t is SocketTimeoutException) {
            msg = "请求网络超时"
        } else if (t is HttpException) {
            msg = convertStatusCode(t)
        } else if (t is JsonParseException || t is ParseException || t is JSONException || t is JsonIOException) {
            msg = "数据解析错误"
        } else if (t is ConnectException) {
            msg = "连接服务器失败，请检查网络~"
        }

        FastUtils.makeText(context, msg)
        return t
    }

    private fun convertStatusCode(httpException: HttpException): String? {
        return when {
            httpException.code() == 500 -> {
                "服务器发生错误"
            }

            httpException.code() == 404 -> {
                "请求地址不存在"
            }

            httpException.code() == 403 -> {
                // 跳转到登录界面
                "请求被服务器拒绝"
            }

            httpException.code() == 401 -> {
                "未授权"
            }

            httpException.code() == 307 -> {
                "请求被重定向到其他页面"
            }

            else -> {
                httpException.message()
            }
        }
    }
}