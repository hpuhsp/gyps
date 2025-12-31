package com.swallow.fly.base.lifecycle

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.swallow.fly.base.lifecycle.config.FrameworkConfigHolder
import com.swallow.fly.base.lifecycle.config.FrameworkConfigProvider
import com.swallow.fly.base.lifecycle.config.ModuleConfig
import com.swallow.fly.base.lifecycle.config.ModuleConfigProvider
import com.swallow.fly.ext.initLogger
import com.swallow.fly.utils.AppManager
import com.tencent.mmkv.MMKV
import javax.inject.Inject

/**
 * @Description: 基础 Application
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/23
 * @UpdateRemark:   自动初始化框架配置
 *
 * 使用说明：
 * 1. 让你的 Application 继承此类
 * 2. 实现 FrameworkConfigProvider 接口
 * 3. 在 provideConfig() 中使用 DSL 配置框架
 *
 * 示例：
 * ```kotlin
 * @HiltAndroidApp
 * class MyApplication : BaseApplication() {
 *     override fun provideConfig(): FrameworkConfig {
 *         return frameworkConfig {
 *             network {
 *                 baseUrl("https://api.example.com/")
 *             }
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseApplication : MultiDexApplication(), FrameworkConfigProvider {
    /**
     * App生命周期代理类
     */
    private var appDelegate: AppDelegate? = null
    
    /**
     * 模块配置提供者（通过 Hilt 注入）
     * 
     * 各业务模块通过 @Binds @IntoSet @ModuleConfig 注册配置
     */
    @Inject
    @ModuleConfig
    lateinit var moduleConfigProviders: Set<@JvmSuppressWildcards ModuleConfigProvider>

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (appDelegate == null) this.appDelegate = AppDelegate(base)
        this.appDelegate?.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        // ✅ 初始化配置（传入模块配置提供者）
        FrameworkConfigHolder.initialize(this, moduleConfigProviders)

        // 执行配置的生命周期回调
        onFrameworkInitialized()

        initLoggerConfig()
        AppManager.getInstance().init(this)
        initMMKV()
        appDelegate?.onCreate(this)
    }

    /**
     * 初始化MMKV
     */
    private fun initMMKV() {
        val rootDir: String = MMKV.initialize(this)
        println("mmkv root: $rootDir")
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

    /**
     * 框架初始化完成回调
     *
     * 子类可以覆盖此方法，在框架初始化后执行自定义逻辑
     */
    protected open fun onFrameworkInitialized() {
        // 默认空实现
    }
}