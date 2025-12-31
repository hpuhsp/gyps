package com.swallow.fly.base.lifecycle.config

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

/**
 * @Description: 配置提供者 Hilt 模块
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/25
 * @UpdateRemark:   用于安装 Multibinding
 * 
 * 这是一个空的抽象类，用于安装 Hilt Multibinding 机制
 * 各业务模块会在自己的 Module 中通过 @Binds @IntoSet 添加配置
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigProviderModule {
    // 空实现，仅用于安装 Multibinding
}

/**
 * 用于标记模块配置提供者的限定符
 * 
 * 使用示例：
 * ```kotlin
 * @Inject
 * @ModuleConfig
 * lateinit var moduleConfigProviders: Set<@JvmSuppressWildcards ModuleConfigProvider>
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ModuleConfig
