package com.swallow.fly.base.app.config

/**
 * @Description: 框架配置提供者接口
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/23
 * @UpdateRemark:   app 模块需要实现此接口
 * 
 * 使用说明：
 * 在 Application 中实现此接口，提供框架配置
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
interface FrameworkConfigProvider {
    /**
     * 提供框架配置
     * 
     * 使用 DSL 方式配置：
     * ```kotlin
     * override fun provideConfig(): FrameworkConfig {
     *     return frameworkConfig {
     *         network {
     *             baseUrl("https://api.example.com/")
     *         }
     *     }
     * }
     * ```
     */
    fun provideConfig(): FrameworkConfig
}
