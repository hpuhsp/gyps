package com.swallow.gyps.app

import android.content.Context
import androidx.room.Room
import com.swallow.fly.base.lifecycle.AppLifecycle
import com.swallow.fly.base.lifecycle.ConfigModule
import com.swallow.fly.db.AppDataBase
import com.swallow.fly.db.DatabaseConfigBuilder
import com.swallow.fly.ext.logi
import com.swallow.fly.http.di.NetworkConfigBuilder
import com.swallow.fly.image.ImageConfigBuilder
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Singleton

/**
 * 全局配置
 * 
 * @UpdateRemark: 现代化升级 - 类型安全的 Builder + 优先级控制
 * 
 * 配置说明：
 * 1. 实现 ConfigModule 接口的各个配置方法
 * 2. 使用类型安全的 Builder 进行配置，支持链式调用
 * 3. 通过 priority() 方法设置优先级（app 模块通常设置为 100）
 * 4. 只需要覆盖需要配置的方法，其他方法使用默认实现
 */
@Singleton
class GlobalConfiguration : ConfigModule {
    
    /**
     * 配置优先级
     * app 模块的配置优先级最高
     */
    override fun priority(): Int = 100
    
    /**
     * 配置网络层
     */
    override fun configureNetwork(context: Context, builder: NetworkConfigBuilder) {
        // ✅ 类型安全的链式调用
        builder
            .baseUrl("http://ny.shuanghui.net:4081/")
            .globalHttpHandler(HttpHandlerImpl())
    }
    
    /**
     * 配置数据库
     */
    override fun configureDatabase(context: Context, builder: DatabaseConfigBuilder) {
        // ✅ 配置自定义数据库
        val dataBase = Room
            .databaseBuilder(context, AppDataBase::class.java, "gyps_test.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        
        builder.database(dataBase)
    }
    
    /**
     * 配置图片加载
     */
    override fun configureImage(context: Context, builder: ImageConfigBuilder) {
        // ✅ 配置图片加载拦截器
        builder.imageLoaderInterceptor(object : Interceptor {
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
        })
    }
    
    /**
     * 注入模块生命周期
     */
    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {
        lifecycleList.add(AppLifecycleImpl())
    }
}