package com.hnsh.core.base.app

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.hnsh.core.base.app.parse.ManifestParser
import com.hnsh.core.base.app.config.GlobalConfigModule
import com.hnsh.core.ext.logd
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:57
 * @UpdateRemark:   更新说明：
 */
@Singleton
class AppDelegate(val context: Context) : AppLifecycle {
    private val mAppLifecycleList = ArrayList<AppLifecycle>()
    private var mModules: List<ConfigModule> = ManifestParser(context).parse()

    init {
        //用反射, 将 AndroidManifest.xml 中带有 ModuleConfig 标签的 class 转成对象集合（List<ModuleConfig>）
        //遍历之前获得的集合, 执行每一个 ModuleConfig 实现类的某些方法
        for (module in mModules) {
            module.injectModulesLifecycle(context, mAppLifecycleList)
        }
    }

    override fun attachBaseContext(base: Context) {
        for (lifecycle in mAppLifecycleList) {
            lifecycle.attachBaseContext(base)
        }
    }

    override fun onCreate(application: Application) {
        // 网络库全局配置
        DaggerAppComponent
            .builder()
            .application(application)
            .globalConfigModule(getGlobalConfigModule(application, mModules)) //全局配置
            .build()
            .inject(this)
        // 执行所有模块的初始化步骤
        for (lifecycle in mAppLifecycleList) {
            lifecycle.onCreate(application)
        }
    }

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface GlobalConfigModuleEntryPoint {
        fun initGlobalConfigModule(configModule: GlobalConfigModule)
    }

    /**
     * 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     * 需要在AndroidManifest中声明[ConfigModule]的实现类,和Glide的配置方式相似
     *
     * @return GlobalConfigModule
     */
    private fun getGlobalConfigModule(
        context: Context,
        modules: List<ConfigModule>
    ): GlobalConfigModule {
        val builder: GlobalConfigModule.Builder = GlobalConfigModule.builder()
        //遍历 ConfigModule 集合, 给全局配置 GlobalConfigModule 添加参数
        for (module in modules) {
            module.applyOptions(context, builder)
        }
        return GlobalConfigModule.getInstance(builder)
    }

    override fun onTerminate(application: Application) {
        if (!mAppLifecycleList.isNullOrEmpty()) {
            for (lifecycle in mAppLifecycleList) {
                lifecycle.onTerminate(application)
            }
        }
    }
}