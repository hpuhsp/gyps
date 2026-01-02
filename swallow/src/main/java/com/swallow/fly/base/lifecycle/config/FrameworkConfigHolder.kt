package com.swallow.fly.base.lifecycle.config

import android.app.Application
import android.content.Context
import com.swallow.fly.db.DatabaseConfigBuilder
import com.swallow.fly.http.di.NetworkConfigBuilder
import com.swallow.fly.image.ImageConfigBuilder
import com.swallow.fly.log.LogConfigBuilder

/**
 * @Description: 框架配置持有者（单例）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/23
 * @UpdateRemark:   支持多模块配置合并
 * 
 * 职责：
 * 1. 持有 app 主配置和各模块配置
 * 2. 按优先级合并配置
 * 3. 提供统一的配置应用接口
 * 
 * 使用说明：
 * 在 Application.onCreate() 中调用 initialize(application, moduleProviders)
 */
object FrameworkConfigHolder {
    
    @Volatile
    private var appConfig: FrameworkConfig? = null
    
    @Volatile
    private var moduleConfigs: Set<ModuleConfigProvider> = emptySet()

    @Volatile
    private var cachedConfig: SwallowConfig? = null
    
    /**
     * 初始化配置
     * 
     * @param application 应用实例（必须实现 FrameworkConfigProvider）
     * @param providers 通过 Hilt 注入的模块配置提供者
     * @throws IllegalArgumentException 如果 Application 未实现 FrameworkConfigProvider
     */
    fun initialize(
        application: Application,
        providers: Set<ModuleConfigProvider> = emptySet()
    ) {
        if (appConfig == null) {
            synchronized(this) {
                if (appConfig == null) {
                    // 1. 获取 app 主配置
                    if (application !is FrameworkConfigProvider) {
                        throw IllegalArgumentException(
                            "Application must implement FrameworkConfigProvider. " +
                            "Please make your Application class implement FrameworkConfigProvider interface."
                        )
                    }
                    appConfig = application.provideConfig()
                    
                    // 2. 保存模块配置
                    moduleConfigs = providers
                    
                    // 3. 构建并缓存最终配置
                    cachedConfig = buildConfig(application)
                    
                    // 4. 日志输出配置信息
                    logConfigInfo()
                }
            }
        }
    }

    /**
     * 构建合并后的配置
     */
    private fun buildConfig(context: Context): SwallowConfig {
        val config = SwallowConfig()
        
        // 1. 按优先级从低到高应用模块配置
        moduleConfigs.sortedBy { it.priority }.forEach { provider ->
            val moduleConfig = provider.provideConfig()
            moduleConfig.networkConfig.invoke(context, config.network)
            moduleConfig.databaseConfig.invoke(context, config.database)
            moduleConfig.imageConfig.invoke(context, config.image)
            moduleConfig.logConfig.invoke(context, config.log)
        }
        
        // 2. 最后应用 app 主配置（优先级最高）
        appConfig?.let {
            it.networkConfig.invoke(context, config.network)
            it.databaseConfig.invoke(context, config.database)
            it.imageConfig.invoke(context, config.image)
            it.logConfig.invoke(context, config.log)
        }
        
        return config
    }

    /**
     * 获取合并后的配置快照
     */
    fun provideConfig(): SwallowConfig {
        return cachedConfig ?: throw IllegalStateException("FrameworkConfigHolder not initialized")
    }
    
    /**
     * 获取配置（已废弃，使用 applyXxxConfig 方法）
     * 
     * @deprecated 使用 applyNetworkConfig/applyDatabaseConfig 等方法
     */
    @Deprecated(
        message = "Use applyNetworkConfig/applyDatabaseConfig instead",
        replaceWith = ReplaceWith("applyNetworkConfig(context, builder)")
    )
    fun getConfig(): FrameworkConfig {
        return appConfig ?: throw IllegalStateException(
            "FrameworkConfigHolder not initialized. " +
            "Call FrameworkConfigHolder.initialize(application) in Application.onCreate()"
        )
    }
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = appConfig != null
    
    /**
     * 重置配置（仅用于测试）
     */
    internal fun reset() {
        appConfig = null
        moduleConfigs = emptySet()
    }
    
    /**
     * 输出配置信息日志
     */
    private fun logConfigInfo() {
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("📦 Framework Configuration Loaded")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("✓ App Config: ${if (appConfig != null) "Loaded" else "None"}")
        println("✓ Module Configs: ${moduleConfigs.size}")
        
        if (moduleConfigs.isNotEmpty()) {
            moduleConfigs
                .sortedByDescending { it.priority }
                .forEach { provider ->
                    println("  - [Priority ${provider.priority}] ${provider.moduleName}")
                }
        }
        
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
}
