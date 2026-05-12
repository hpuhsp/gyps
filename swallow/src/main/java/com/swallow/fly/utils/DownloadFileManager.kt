package com.swallow.fly.utils

import com.swallow.fly.ext.logd
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @Description: 文件下载
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/10/21 11:02
 * @UpdateRemark:   更新说明：
 */
object DownloadFileManager {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 指定Url下载文件
     */
    fun download(url: String, saveFile: File, callBack: StateCallBack) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                logd { "------------下载失败！-------->" }
                callBack.downloadFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body?.byteStream() ?: return
                try {
                    FileOutputStream(saveFile).use { fos ->
                        val buffer = ByteArray(8192)
                        var len: Int
                        while (inputStream.read(buffer).also { len = it } != -1) {
                            fos.write(buffer, 0, len)
                        }
                        fos.flush()
                    }
                    callBack.downloadSuccess(saveFile)
                } catch (e: IOException) {
                    logd { "------------写入文件失败！-------->" }
                    callBack.downloadFailure()
                } finally {
                    inputStream.close()
                }
            }
        })
    }

    /**
     * 下载结果回调
     */
    interface StateCallBack {
        fun downloadFailure()
        fun downloadSuccess(file: File)
    }
}