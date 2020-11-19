package com.swallow.fly.base

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.swallow.fly.BuildConfig
import com.swallow.fly.base.app.AppDelegate
import com.swallow.fly.ext.initLogger
import com.swallow.fly.utils.AppManager

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/21 16:05
 * @UpdateRemark:   更新说明：
 */
abstract class BaseApplication : MultiDexApplication() {
    /**
     * App生命周期代理类
     */
    var appDelegate: AppDelegate? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (appDelegate == null) this.appDelegate = AppDelegate(base)
        this.appDelegate?.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        initLogger(BuildConfig.DEBUG)
        initARouter()
        AppManager.getInstance().init(this)
        appDelegate?.onCreate(this)
    }

    private fun initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog() // 开启日志
            ARouter.openDebug() // 使用InstantRun的时候，需要打开该开关，上线之后关闭，否则有安全风险
        }
        ARouter.printStackTrace() // 打印日志的时候打印线程堆栈
        ARouter.init(this)
    }

    /**
     * 退出
     */
    override fun onTerminate() {
        super.onTerminate()
        appDelegate?.onTerminate(this)
    }
}