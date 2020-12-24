package com.swallow.gyps.app

import android.app.Application
import android.content.Context
import com.swallow.fly.base.app.AppLifecycle
import javax.inject.Singleton

@Singleton
class AppLifecycleImpl : AppLifecycle {
    override fun attachBaseContext(base: Context) {
    }

    override fun onCreate(application: Application) {
    }

    /**
     * 退出APP
     */
    override fun onTerminate(application: Application) {
    }
}