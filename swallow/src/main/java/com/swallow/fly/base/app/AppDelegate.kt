package com.swallow.fly.base.app

import android.app.Application
import android.content.Context
import com.swallow.fly.base.app.parse.ManifestParser
import javax.inject.Singleton

/**
 * @Description: 应用生命周期代理
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:57
 * @UpdateRemark:   更新说明：现代化升级 - 移除旧的 Dagger 代码，使用 Hilt
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
        // ✅ Hilt 会自动处理依赖注入，无需手动创建 Component
        // AppConfigModule 会自动读取 ManifestParser 中的配置
        
        // 执行所有模块的初始化步骤
        for (lifecycle in mAppLifecycleList) {
            lifecycle.onCreate(application)
        }
    }

    override fun onTerminate(application: Application) {
        if (!mAppLifecycleList.isNullOrEmpty()) {
            for (lifecycle in mAppLifecycleList) {
                lifecycle.onTerminate(application)
            }
        }
    }
}