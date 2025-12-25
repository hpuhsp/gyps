package com.swallow.fly.base.lifecycle.config

import android.app.Application

/**
 * @Description: 框架配置持有者（单例）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/23
 * @UpdateRemark:   在 Application 启动时初始化
 * 
 * 职责：
 * 1. 持有框架配置
 * 2. 提供全局访问点
 * 3. 确保配置只初始化一次
 * 
 * 使用说明：
 * 在 Application.onCreate() 中调用 initialize(application)
 */
object FrameworkConfigHolder {
    
    @Volatile
    private var config: FrameworkConfig? = null
    
    /**
     * 初始化配置
     * 
     * 在 Application.onCreate() 中调用
     * 
     * @param application 应用实例（必须实现 FrameworkConfigProvider）
     * @throws IllegalArgumentException 如果 Application 未实现 FrameworkConfigProvider
     */
    fun initialize(application: Application) {
        if (config == null) {
            synchronized(this) {
                if (config == null) {
                    if (application !is FrameworkConfigProvider) {
                        throw IllegalArgumentException(
                            "Application must implement FrameworkConfigProvider. " +
                            "Please make your Application class implement FrameworkConfigProvider interface."
                        )
                    }
                    config = application.provideConfig()
                }
            }
        }
    }
    
    /**
     * 获取配置
     * 
     * @return FrameworkConfig 实例
     * @throws IllegalStateException 如果未初始化
     */
    fun getConfig(): FrameworkConfig {
        return config ?: throw IllegalStateException(
            "FrameworkConfigHolder not initialized. " +
            "Call FrameworkConfigHolder.initialize(application) in Application.onCreate()"
        )
    }
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = config != null
    
    /**
     * 重置配置（仅用于测试）
     */
    internal fun reset() {
        config = null
    }
}
