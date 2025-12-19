package com.swallow.fly.http.interceptor

import com.swallow.fly.http.printer.CharacterHandler
import com.swallow.fly.http.printer.FormatPrinter
import com.swallow.fly.utils.UrlEncoderUtils
import com.swallow.fly.utils.ZipHelper
import okhttp3.*
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 请求拦截器 - 现代化版本
 * 
 * 负责：
 * - 打印请求和响应日志
 * - 处理请求异常
 * - 支持自定义日志格式化
 * 
 * @Description: HTTP 请求拦截器，用于日志打印和请求处理
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/29 14:36
 * @UpdateRemark: 2024 - 现代化升级：优化代码结构，改进日志配置，使用 Kotlin DSL
 */
@Singleton
class RequestInterceptor @Inject constructor(
    private val mHandler: GlobalHttpHandler?,
    private val mPrinter: FormatPrinter?,
    val printLevel: Level
) : Interceptor {

    /**
     * 日志打印级别
     */
    enum class Level {
        /** 不打印日志 */
        NONE,
        
        /** 只打印请求信息 */
        REQUEST,
        
        /** 只打印响应信息 */
        RESPONSE,
        
        /** 打印所有信息 */
        ALL
    }

    /**
     * 拦截请求并处理
     * 
     * @param chain 拦截器链
     * @return 响应结果
     * @throws IOException 网络异常
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 判断是否需要打印请求日志
        val shouldLogRequest = printLevel == Level.ALL || printLevel == Level.REQUEST
        if (shouldLogRequest) {
            logRequest(request)
        }
        
        // 判断是否需要打印响应日志
        val shouldLogResponse = printLevel == Level.ALL || printLevel == Level.RESPONSE
        val startTime = if (shouldLogResponse) System.nanoTime() else 0
        
        // 执行请求
        val originalResponse = try {
            chain.proceed(request)
        } catch (e: Exception) {
            // 尝试通过 handler 处理异常
            val redirectResponse = mHandler?.redirectRequest(chain, request, e)
            if (redirectResponse != null) {
                return redirectResponse
            } else {
                Timber.w(e, "HTTP Request Failed: ${request.url}")
                throw e
            }
        }
        
        val endTime = if (shouldLogResponse) System.nanoTime() else 0
        val responseBody = originalResponse.body
        
        // 打印响应日志
        var bodyString: String? = null
        if (shouldLogResponse && responseBody != null) {
            bodyString = if (isParseable(responseBody.contentType())) {
                printResult(request, originalResponse)
            } else {
                null
            }
            
            logResponse(originalResponse, bodyString, TimeUnit.NANOSECONDS.toMillis(endTime - startTime))
        }
        
        // 通过 handler 处理响应
        return mHandler?.onHttpResultResponse(bodyString, chain, originalResponse) 
            ?: originalResponse
    }
    
    /**
     * 打印请求日志
     */
    private fun logRequest(request: Request) {
        val body = request.body
        if (body != null && isParseable(body.contentType())) {
            mPrinter?.printJsonRequest(request, parseParams(request))
        } else {
            mPrinter?.printFileRequest(request)
        }
    }
    
    /**
     * 打印响应日志
     */
    private fun logResponse(response: Response, bodyString: String?, duration: Long) {
        val request = response.request
        val segmentList = request.url.encodedPathSegments
        val header = response.headers.toString()
        val code = response.code
        val isSuccessful = response.isSuccessful
        val message = response.message
        val url = request.url.toString()
        val responseBody = response.body
        
        if (responseBody != null && isParseable(responseBody.contentType())) {
            mPrinter?.printJsonResponse(
                duration, isSuccessful, code, header, 
                responseBody.contentType(), bodyString, segmentList, message, url
            )
        } else {
            mPrinter?.printFileResponse(
                duration, isSuccessful, code, header, segmentList, message, url
            )
        }
    }

    /**
     * 打印响应结果
     *
     * @param request 请求对象
     * @param response 响应对象
     * @return 解析后的响应结果字符串
     * @throws IOException IO 异常
     */
    @Throws(IOException::class)
    private fun printResult(request: Request, response: Response): String? {
        return try {
            // 读取服务器返回的结果
            val responseBody = response.newBuilder().build().body ?: return null
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body
            val buffer = source.buffer()

            // 获取 content 的压缩类型
            val encoding = response.headers["Content-Encoding"]
            val clone = buffer.clone()

            // 解析 response content
            parseContent(responseBody, encoding, clone)
        } catch (e: IOException) {
            Timber.e(e, "Failed to parse response body")
            "{\"error\": \"${e.message}\"}"
        }
    }

    /**
     * 解析服务器响应的内容
     *
     * @param responseBody 响应体
     * @param encoding 编码类型
     * @param clone 克隆后的服务器响应内容
     * @return 解析后的响应结果
     */
    private fun parseContent(
        responseBody: ResponseBody?,
        encoding: String?,
        clone: Buffer
    ): String? {
        var charset = Charset.forName("UTF-8")
        val contentType = responseBody?.contentType()
        if (contentType != null) {
            charset = contentType.charset(charset) ?: charset
        }
        
        return when {
            encoding.equals("gzip", ignoreCase = true) -> {
                // content 使用 gzip 压缩
                ZipHelper.decompressForGzip(clone.readByteArray(), convertCharset(charset))
            }
            encoding.equals("zlib", ignoreCase = true) -> {
                // content 使用 zlib 压缩
                ZipHelper.decompressToStringForZlib(clone.readByteArray(), convertCharset(charset))
            }
            else -> {
                // content 没有被压缩，或者使用其他未知压缩方式
                clone.readString(charset)
            }
        }
    }

    /**
     * 解析请求服务器的请求参数
     *
     * @param request 请求对象
     * @return 解析后的请求信息
     * @throws UnsupportedEncodingException 编码异常
     */
    @Throws(UnsupportedEncodingException::class)
    fun parseParams(request: Request): String {
        return try {
            val body = request.newBuilder().build().body ?: return ""
            val requestBuffer = Buffer()
            body.writeTo(requestBuffer)
            
            var charset = Charset.forName("UTF-8")
            val contentType = body.contentType()
            if (contentType != null) {
                charset = contentType.charset(charset) ?: charset
            }
            
            var json = requestBuffer.readString(charset)
            if (UrlEncoderUtils.hasUrlEncoded(json)) {
                json = URLDecoder.decode(json, convertCharset(charset))
            }
            
            CharacterHandler.jsonFormat(json) ?: ""
        } catch (e: IOException) {
            Timber.e(e, "Failed to parse request params")
            "{\"error\": \"${e.message}\"}"
        }
    }

    companion object {
        /**
         * 判断 MediaType 是否可以解析
         *
         * @param mediaType 媒体类型
         * @return true 表示可以解析
         */
        fun isParseable(mediaType: MediaType?): Boolean {
            if (mediaType?.type == null) return false
            
            return isText(mediaType) || isPlain(mediaType) || isJson(mediaType) 
                || isForm(mediaType) || isHtml(mediaType) || isXml(mediaType)
        }

        /**
         * 判断是否为 text 类型
         */
        fun isText(mediaType: MediaType?): Boolean {
            return mediaType?.type == "text"
        }

        /**
         * 判断是否为 plain 类型
         */
        fun isPlain(mediaType: MediaType?): Boolean {
            return mediaType?.subtype?.lowercase()?.contains("plain") == true
        }

        /**
         * 判断是否为 json 类型
         */
        fun isJson(mediaType: MediaType?): Boolean {
            return mediaType?.subtype?.lowercase()?.contains("json") == true
        }

        /**
         * 判断是否为 xml 类型
         */
        fun isXml(mediaType: MediaType?): Boolean {
            return mediaType?.subtype?.lowercase()?.contains("xml") == true
        }

        /**
         * 判断是否为 html 类型
         */
        fun isHtml(mediaType: MediaType?): Boolean {
            return mediaType?.subtype?.lowercase()?.contains("html") == true
        }

        /**
         * 判断是否为 form 类型
         */
        fun isForm(mediaType: MediaType?): Boolean {
            return mediaType?.subtype?.lowercase()?.contains("x-www-form-urlencoded") == true
        }

        /**
         * 转换 Charset 为字符串
         */
        fun convertCharset(charset: Charset?): String {
            val s = charset.toString()
            val i = s.indexOf("[")
            return if (i == -1) s else s.substring(i + 1, s.length - 1)
        }
    }
}