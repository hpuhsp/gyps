package com.swallow.fly.base

import android.content.Context
import androidx.multidex.MultiDexApplication
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
        initLoggerConfig()
        AppManager.getInstance().init(this)
        appDelegate?.onCreate(this)
    }

    /**
     * can override
     *初始化日志输出工具类（可根据具体需要进行重写）,默认为true
     */
    open fun initLoggerConfig() {
        initLogger(true)
    }

    /**
     * 退出
     */
    override fun onTerminate() {
        super.onTerminate()
        appDelegate?.onTerminate(this)
    }
}