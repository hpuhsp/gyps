package com.swallow.fly.base.lifecycle.config

import android.content.Context
import com.swallow.fly.db.DatabaseConfigBuilder
import com.swallow.fly.http.di.NetworkConfigBuilder
import com.swallow.fly.image.ImageConfigBuilder
import com.swallow.fly.log.LogConfigBuilder

/**
 * @Description: 框架配置 DSL
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/23
 * @UpdateRemark:   使用 Kotlin DSL 提供类型安全的配置方式
 * 
 * 使用示例：
 * ```kotlin
 * val config = frameworkConfig {
 *     network {
 *         baseUrl("https://api.example.com/")
 *         globalHttpHandler(MyHttpHandler())
 *     }
 *     database {
 *         database(myDatabase)
 *     }
 * }
 * ```
 */
class FrameworkConfig internal constructor() {
    
    internal var networkConfig: (Context, NetworkConfigBuilder) -> Unit = { _, _ -> }
    internal var databaseConfig: (Context, DatabaseConfigBuilder) -> Unit = { _, _ -> }
    internal var logConfig: (Context, LogConfigBuilder) -> Unit = { _, _ -> }
    internal var imageConfig: (Context, ImageConfigBuilder) -> Unit = { _, _ -> }
    
    /**
     * 配置网络层
     */
    fun network(block: NetworkConfigBuilder.(Context) -> Unit) {
        networkConfig = { context, builder -> builder.block(context) }
    }
    
    /**
     * 配置数据库
     */
    fun database(block: DatabaseConfigBuilder.(Context) -> Unit) {
        databaseConfig = { context, builder -> builder.block(context) }
    }
    
    /**
     * 配置日志
     */
    fun log(block: LogConfigBuilder.(Context) -> Unit) {
        logConfig = { context, builder -> builder.block(context) }
    }
    
    /**
     * 配置图片加载
     */
    fun image(block: ImageConfigBuilder.(Context) -> Unit) {
        imageConfig = { context, builder -> builder.block(context) }
    }
}

/**
 * DSL 构建器函数
 */
fun frameworkConfig(block: FrameworkConfig.() -> Unit): FrameworkConfig {
    return FrameworkConfig().apply(block)
}
