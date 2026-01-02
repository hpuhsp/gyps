package com.swallow.fly.http.engine

/**
 * HTTP 引擎抽象接口
 * 
 * 设计目标：
 * - 解耦网络库依赖，支持 Retrofit、Ktor、OkHttp 等多种实现
 * - 为未来技术栈切换提供灵活性
 * - 保持 API 稳定，用户代码无需修改
 * 
 * @Description: 网络请求引擎抽象层
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2026/01/02
 * @UpdateRemark: 初始版本 - 支持 Service 创建和请求执行
 */
interface HttpEngine {
    
    /**
     * 创建网络服务接口实例
     * 
     * @param service 服务接口的 Class 对象
     * @return 服务接口的实现实例
     * 
     * 使用示例：
     * ```kotlin
     * val userService = httpEngine.createService(UserService::class.java)
     * val user = userService.getUser("123")
     * ```
     */
    fun <T> createService(service: Class<T>): T
    
    /**
     * 取消指定标签的请求
     * 
     * @param tag 请求标签
     * 
     * 使用示例：
     * ```kotlin
     * httpEngine.cancelRequest("user_request")
     * ```
     */
    fun cancelRequest(tag: String)
    
    /**
     * 取消所有进行中的请求
     */
    fun cancelAllRequests()
}
