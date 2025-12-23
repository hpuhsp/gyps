package com.swallow.fly.log

import com.swallow.fly.http.interceptor.RequestInterceptor
import com.swallow.fly.http.printer.FormatPrinter

/**
 * @Description: 日志配置构建器
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的配置构建器
 * 
 * 使用示例：
 * ```kotlin
 * override fun configureLog(context: Context, builder: LogConfigBuilder) {
 *     builder
 *         .printHttpLogLevel(RequestInterceptor.Level.ALL)
 *         .formatPrinter(MyFormatPrinter())
 * }
 * ```
 */
class LogConfigBuilder {
    
    var printHttpLogLevel: RequestInterceptor.Level? = null
        private set
    
    var formatPrinter: FormatPrinter? = null
        private set
    
    /**
     * 配置 HTTP 日志打印级别
     * 
     * @param level 日志级别
     * @return 当前构建器，支持链式调用
     */
    fun printHttpLogLevel(level: RequestInterceptor.Level): LogConfigBuilder {
        this.printHttpLogLevel = level
        return this
    }
    
    /**
     * 配置格式化打印器
     * 
     * @param printer 格式化打印器
     * @return 当前构建器，支持链式调用
     */
    fun formatPrinter(printer: FormatPrinter): LogConfigBuilder {
        this.formatPrinter = printer
        return this
    }
}
