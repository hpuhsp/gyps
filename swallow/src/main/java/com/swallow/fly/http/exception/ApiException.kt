package com.swallow.fly.http.exception

/**
 * API 业务异常
 * 
 * 用于封装服务器返回的业务错误码和错误信息
 * 
 * 使用场景:
 * - BaseResponse.code != 0 时抛出
 * - 业务逻辑错误(如余额不足、权限不足等)
 * 
 * 与网络异常的区别:
 * - 网络异常: IOException, SocketTimeoutException 等
 * - 业务异常: ApiException (HTTP 请求成功,但业务逻辑失败)
 * 
 * @param code 业务错误码
 * @param message 错误信息
 * 
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 2.0.0
 */
data class ApiException(
    val code: Int,
    override val message: String
) : Exception(message) {
    
    companion object {
        // 常见业务错误码
        const val CODE_UNAUTHORIZED = 401
        const val CODE_FORBIDDEN = 403
        const val CODE_NOT_FOUND = 404
        const val CODE_SERVER_ERROR = 500
        const val CODE_SERVICE_UNAVAILABLE = 503
        
        // 自定义业务错误码
        const val CODE_TOKEN_EXPIRED = 1001
        const val CODE_INVALID_PARAMS = 1002
        const val CODE_BUSINESS_ERROR = 1003
    }
    
    /**
     * 是否为认证错误
     */
    fun isAuthError(): Boolean {
        return code == CODE_UNAUTHORIZED || code == CODE_TOKEN_EXPIRED
    }
    
    /**
     * 是否为服务器错误
     */
    fun isServerError(): Boolean {
        return code >= 500
    }
    
    /**
     * 是否为客户端错误
     */
    fun isClientError(): Boolean {
        return code in 400..499
    }
}
