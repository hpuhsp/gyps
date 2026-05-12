package com.swallow.fly.http.result

/**
 * @Description: 全局响应成功判断配置
 * @Author:   Hsp
 * @CreateTime: 2024/12/22
 *
 * 由框架初始化时注入，业务层通过 NetworkConfigBuilder.successChecker 配置。
 * BaseResponse.isSuccessful() 优先委托此处，未配置时 fallback 到默认逻辑。
 */
object ResponseConfig {

    /**
     * 自定义成功判断逻辑，null 时使用 BaseResponse 默认实现
     */
    @Volatile
    var successChecker: ((code: Int, status: String) -> Boolean)? = null
        internal set
}
