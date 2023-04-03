package com.swallow.gyps

import com.alibaba.android.arouter.launcher.ARouter
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.swallow.fly.base.BaseApplication
import com.swallow.fly.ext.initLogger
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
        initARouterConfig()
    }
    
    /**
     * 初始化讯飞SDK
     */
    private fun initMscSdk() {
        SpeechUtility.createUtility(this, "${SpeechConstant.APPID}=${AppConfig.MSC_APP_ID}")
    }
    
    override fun initLoggerConfig() {
        initLogger(BuildConfig.DEBUG)
    }
    
    /**
     * 初始化ARouter路由框架（可根据具体需要进行重写）
     */
    private fun initARouterConfig() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog() // 开启日志
            ARouter.openDebug() // 使用InstantRun的时候，需要打开该开关，上线之后关闭，否则有安全风险
        }
        ARouter.printStackTrace() // 打印日志的时候打印线程堆栈
        ARouter.init(this)
    }
}