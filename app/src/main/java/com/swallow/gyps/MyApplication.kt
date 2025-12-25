package com.swallow.gyps

import androidx.room.Room
import com.alibaba.android.arouter.launcher.ARouter
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.swallow.fly.base.lifecycle.BaseApplication
import com.swallow.fly.base.lifecycle.config.FrameworkConfig
import com.swallow.fly.base.lifecycle.config.frameworkConfig
import com.swallow.fly.db.AppDataBase
import com.swallow.fly.ext.initLogger
import com.swallow.gyps.app.HttpHandlerImpl
import com.swallow.gyps.common.AppConfig
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/19 11:24
 * @UpdateRemark:   更新说明：
 */
@HiltAndroidApp
class MyApplication : BaseApplication() {

    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {

            // 配置网络层
            network {
                baseUrl("http://ny.shuanghui.net:4081/")
                globalHttpHandler(HttpHandlerImpl())
            }

            // 配置数据库
            database {
                val db = Room.databaseBuilder(
                    it,  // it 是 Context
                    AppDataBase::class.java,
                    "gyps_test.db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()

                database(db)
            }

            // 配置图片加载
            image {
                imageLoaderInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request().newBuilder()
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .addHeader("platform", "Android")
                            .addHeader("Test", "HaHa")
                            .build()
                        return chain.proceed(request)
                    }
                })
            }

            // 配置日志（可选）
            // log {
            //     printHttpLogLevel(RequestInterceptor.Level.ALL)
            // }
        }
    }

    override fun onFrameworkInitialized() {
        super.onFrameworkInitialized()

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