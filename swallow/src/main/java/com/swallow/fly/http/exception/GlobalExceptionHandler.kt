package com.swallow.fly.http.exception

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description: 全局异常处理器
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2024/12
 * @UpdateRemark: 统一处理各种异常并转换为 AppException
 */
@Singleton
class GlobalExceptionHandler @Inject constructor() {
    
    /**
     * 处理异常并转换为 AppException
     * @param throwable 原始异常
     * @return AppException 转换后的应用异常
     */
    fun handleException(throwable: Throwable): AppException {
        return when (throwable) {
            // 网络异常
            is IOException -> {
                if (throwable is SocketTimeoutException) {
                    AppException.TimeoutException(throwable)
                } else {
                    AppException.NetworkException(throwable)
                }
            }
            
            // HTTP 异常
            is HttpException -> {
                when (throwable.code()) {
                    401 -> AppException.UnauthorizedException()
                    403 -> AppException.ForbiddenException()
                    404 -> AppException.NotFoundException()
                    in 500..599 -> AppException.ServerException()
                    else -> AppException.HttpException(throwable.code(), throwable.message())
                }
            }
            
            // JSON 解析异常
            is JsonSyntaxException -> AppException.ParseException(throwable)
            
            // 已经是 AppException
            is AppException -> throwable
            
            // 其他未知异常
            else -> AppException.UnknownException(throwable)
        }
    }
    
    /**
     * 获取异常的用户友好消息
     * @param exception 异常
     * @return 用户友好的错误消息
     */
    fun getUserFriendlyMessage(exception: Throwable): String {
        val appException = if (exception is AppException) {
            exception
        } else {
            handleException(exception)
        }
        
        return appException.message ?: "未知错误"
    }
}
