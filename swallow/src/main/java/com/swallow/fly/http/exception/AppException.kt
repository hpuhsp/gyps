package com.swallow.fly.http.exception

/**
 * @Description: 应用异常密封类
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2024/12
 * @UpdateRemark: 统一异常类型定义
 */
sealed class AppException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * 网络连接异常
     */
    class NetworkException(cause: Throwable) : AppException("网络连接失败", cause)
    
    /**
     * 未授权异常
     */
    class UnauthorizedException : AppException("未授权，请重新登录")
    
    /**
     * 无权限异常
     */
    class ForbiddenException : AppException("无权限访问")
    
    /**
     * 资源不存在异常
     */
    class NotFoundException : AppException("请求的资源不存在")
    
    /**
     * 服务器错误异常
     */
    class ServerException : AppException("服务器错误")
    
    /**
     * HTTP 异常
     */
    class HttpException(val code: Int, message: String?) : AppException("HTTP错误: $code - $message")
    
    /**
     * 数据解析异常
     */
    class ParseException(cause: Throwable) : AppException("数据解析失败", cause)
    
    /**
     * 超时异常
     */
    class TimeoutException(cause: Throwable) : AppException("请求超时", cause)
    
    /**
     * 未知异常
     */
    class UnknownException(cause: Throwable) : AppException("未知错误", cause)
}
