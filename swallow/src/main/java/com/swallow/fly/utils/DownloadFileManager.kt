package com.swallow.fly.utils

import com.swallow.fly.ext.logd
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * @Description: 文件下载
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/10/21 11:02
 * @UpdateRemark:   更新说明：
 */
object DownloadFileManager {

    /**
     * 指定Url下载文件
     */
    fun download(url: String, saveFile: File, callBack: StateCallBack) {
        val mOkHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).build()
        // 异步线程
        mOkHttpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    logd { "------------下载失败！-------->" }
                    callBack.downloadFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    val inputStream: InputStream = response.body?.byteStream() ?: return
                    var fileOutputStream: FileOutputStream? = null
                    fileOutputStream = FileOutputStream(saveFile)
                    FileUtils
                    val buffer = ByteArray(2048)
                    var len = 0
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        fileOutputStream.write(buffer, 0, len)
                    }
                    fileOutputStream.flush()
                    callBack.downloadSuccess(saveFile)
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