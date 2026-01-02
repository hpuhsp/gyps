package com.swallow.fly.http.di

import com.swallow.fly.http.engine.HttpEngine
import com.swallow.fly.http.engine.RetrofitHttpEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * HTTP Engine 依赖注入模块
 * 
 * @Description: 提供 HttpEngine 的依赖绑定
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2026/01/02
 * @UpdateRemark:
 *   - 当前默认使用 RetrofitHttpEngine
 *   - 未来可通过配置切换到其他实现（KtorHttpEngine、OkHttpEngine 等）
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HttpEngineModule {
    
    /**
     * 绑定 HttpEngine 实现
     * 
     * 当前默认使用 Retrofit 实现
     * 未来扩展方式：
     * 1. 创建新的 Engine 实现（如 KtorHttpEngine）
     * 2. 在 SwallowConfig 中添加 engine 配置项
     * 3. 使用 @Provides 方法根据配置动态选择实现
     * 
     * 示例（未来扩展）：
     * ```kotlin
     * @Provides
     * @Singleton
     * fun provideHttpEngine(
     *     config: SwallowConfig,
     *     retrofitEngine: RetrofitHttpEngine,
     *     ktorEngine: KtorHttpEngine
     * ): HttpEngine {
     *     return when (config.network.engine) {
     *         HttpEngineType.RETROFIT -> retrofitEngine
     *         HttpEngineType.KTOR -> ktorEngine
     *         else -> retrofitEngine
     *     }
     * }
     * ```
     */
    @Binds
    @Singleton
    abstract fun bindHttpEngine(impl: RetrofitHttpEngine): HttpEngine
}
