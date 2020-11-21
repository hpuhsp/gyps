package com.swallow.gyps

import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.swallow.fly.base.BaseApplication
import com.swallow.gyps.common.AppConfig
import dagger.hilt.android.HiltAndroidApp

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/19 11:24
 * @UpdateRemark:   更新说明：
 */
@HiltAndroidApp
class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        initMscSdk()
    }

    /**
     * 初始化讯飞SDK
     */
    private fun initMscSdk() {
        SpeechUtility.createUtility(this, "${SpeechConstant.APPID}=${AppConfig.MSC_APP_ID}")
    }
}