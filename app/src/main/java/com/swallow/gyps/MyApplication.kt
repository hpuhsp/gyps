package com.swallow.gyps

import androidx.room.Room
import com.swallow.fly.base.lifecycle.BaseApplication
import com.swallow.fly.base.lifecycle.config.FrameworkConfig
import com.swallow.fly.base.lifecycle.config.frameworkConfig
import com.swallow.fly.db.AppDataBase
import com.swallow.fly.ext.initLogger
import com.swallow.gyps.app.HttpHandlerImpl
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
                    .allowMainThreadQueries()  // ⚠️ 仅限 sample/debug，生产环境请移除
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
    }

    override fun initLoggerConfig() {
        initLogger(BuildConfig.DEBUG)
    }
}