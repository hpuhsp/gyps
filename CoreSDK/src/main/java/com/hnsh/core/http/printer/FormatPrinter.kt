package com.hnsh.core.http.printer

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import okhttp3.MediaType
import okhttp3.Request

/**
 * @Description: 规范打输出，可自行定义打印格式
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/29 14:59
 * @UpdateRemark:   更新说明：
 */
interface FormatPrinter {
    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 可以解析的情况
     *
     * @param request
     * @param bodyString 发送给服务器的请求体中的数据(已解析)
     */
    fun printJsonRequest(
        @NonNull request: Request,
        @NonNull bodyString: String
    )

    /**
     * 打印网络请求信息, 当网络请求时 {[okhttp3.RequestBody]} 为 `null` 或不可解析的情况
     *
     * @param request
     */
    fun printFileRequest(@NonNull request: Request)

    /**
     * 打印网络响应信息, 当网络响应时 {[okhttp3.ResponseBody]} 可以解析的情况
     *
     * @param chainMs      服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code         响应码
     * @param headers      请求头
     * @param contentType  服务器返回数据的数据类型
     * @param bodyString   服务器返回的数据(已解析)
     * @param segments     域名后面的资源地址
     * @param message      响应信息
     * @param responseUrl  请求地址
     */
    fun printJsonResponse(
        chainMs: Long,
        isSuccessful: Boolean,
        code: Int,
        @NonNull headers: String,
        @Nullable contentType: MediaType?,
        @Nullable bodyString: String?,
        @NonNull segments: List<String>,
        @NonNull message: String,
        @NonNull responseUrl: String
    )

    /**
     * 打印网络响应信息, 当网络响应时 {[okhttp3.ResponseBody]} 为 `null` 或不可解析的情况
     *
     * @param chainMs      服务器响应耗时(单位毫秒)
     * @param isSuccessful 请求是否成功
     * @param code         响应码
     * @param headers      请求头
     * @param segments     域名后面的资源地址
     * @param message      响应信息
     * @param responseUrl  请求地址
     */
    fun printFileResponse(
        chainMs: Long,
        isSuccessful: Boolean,
        code: Int,
        @NonNull headers: String,
        @NonNull segments: List<String>,
        @NonNull message: String,
        @NonNull responseUrl: String
    )
}