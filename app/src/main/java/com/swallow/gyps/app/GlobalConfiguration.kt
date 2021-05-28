package com.swallow.gyps.app

import android.content.Context
import androidx.room.Room
import com.swallow.fly.base.app.AppLifecycle
import com.swallow.fly.base.app.ConfigModule
import com.swallow.fly.base.app.config.GlobalConfigModule
import com.swallow.fly.db.AppDataBase
import com.swallow.fly.http.BaseUrl
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
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
                    return "http://www.github.com/".toHttpUrlOrNull()
                }
            })
            builder.globalHttpHandler(HttpHandlerImpl())
        }
    }

    /**
     * 添加子Module的生命周期监听
     */
    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {
        lifecycleList.add(AppLifecycleImpl())
    }
}