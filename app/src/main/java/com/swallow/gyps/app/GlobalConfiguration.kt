package com.swallow.gyps.app

import android.content.Context
import androidx.room.Room
import com.swallow.fly.base.app.AppLifecycle
import com.swallow.fly.base.app.ConfigModule
import com.swallow.fly.base.app.config.GlobalConfigModule
import com.swallow.fly.db.AppDataBase
import com.swallow.fly.ext.logi
import com.swallow.fly.http.BaseUrl
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Singleton

/**
 * 全局配置
 */
@Singleton
class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context?, builder: GlobalConfigModule.Builder) {
        context?.let {
            // 配置基础数据库
            val dataBase = Room
                .databaseBuilder(it, AppDataBase::class.java, "gyps_test.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
            builder.cacheDB(dataBase)
            // App域名、网络参数配置
            builder.baseurl(object : BaseUrl {
                override fun url(): HttpUrl? {
                    return "http://ny.shuanghui.net:4081/".toHttpUrlOrNull()
                }
            })
            builder.globalHttpHandler(HttpHandlerImpl())
                .imageLoaderInterceptor(
                    object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            logi {
                                "----------------------图片请求-------111------>${
                                    chain.call().request().url
                                }"
                            }
                            logi {
                                "----------------------图片请求----Header----11----->${
                                    chain.call().request()
                                }"
                            }
                            val result = chain.request().newBuilder()
                                .header("Content-Type", "application/json;charset=UTF-8")
                                .addHeader("platform", "Android")
                                .addHeader("Test", "HaHa")
                                .build()
                            logi {
                                "----------------------图片请求----quan----111----->${
                                    result
                                }"
                            }
                            logi {
                                "----------------------图片请求----result----111----->${
                                    result.headers
                                }"
                            }
                            val target = chain.proceed(result)
                            return target
                        }
                    }
                )
        }
    }
    
    /**
     * 添加子Module的生命周期监听
     */
    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {
        lifecycleList.add(AppLifecycleImpl())
    }
}