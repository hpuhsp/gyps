package com.swallow.fly.base.lifecycle.config

/**
 * @Description: 模块配置提供者接口
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/25
 * @UpdateRemark:   基于 Hilt Multibinding 的模块化配置
 * 
 * 使用说明：
 * 1. 各业务模块实现此接口
 * 2. 通过 Hilt @Binds @IntoSet 注册到框架
 * 3. 框架自动收集并按优先级合并配置
 * 
 * 示例：
 * ```kotlin
 * class PaymentConfigProvider @Inject constructor() : ModuleConfigProvider {
 *     override val priority: Int = 80
 *     override val moduleName: String = "Payment"
 *     
 *     override fun provideConfig(): FrameworkConfig {
 *         return frameworkConfig {
 *             network {
 *                 addInterceptor(PaymentInterceptor())
 *             }
 *         }
 *     }
 * }
 * 
 * @Module
 * @InstallIn(SingletonComponent::class)
 * abstract class PaymentModule {
 *     @Binds
 *     @IntoSet
 *     @ModuleConfig
 *     abstract fun bindPaymentConfig(
 *         provider: PaymentConfigProvider
 *     ): ModuleConfigProvider
 * }
 * ```
 */
interface ModuleConfigProvider {
    
    /**
     * 配置优先级
     * 
     * 优先级规则：
     * - app 模块：100（最高优先级，通过 Application.provideConfig()）
     * - 业务模块：50-99（如支付、用户、订单等）
     * - 基础模块：0-49（如日志、统计等）
     * 
     * 注意：数字越大优先级越高，高优先级配置会覆盖低优先级配置
     */
    val priority: Int
    
    /**
     * 模块名称
     * 
     * 用于日志输出和调试，建议使用简短的英文名称
     * 例如：Payment, User, Analytics
     */
    val moduleName: String
    
    /**
     * 提供配置
     * 
     * 使用 frameworkConfig DSL 构建配置
     * 
     * @return FrameworkConfig 实例
     */
    fun provideConfig(): FrameworkConfig
}
