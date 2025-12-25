package com.swallow.fly.base.data

/**
 * @Description: Repository 接口（平台无关）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/21 15:57
 * @UpdateRemark:   
 *   - 2024/12: 添加现代化方法签名
 *   - 为 KMM 跨平台做准备
 */
interface IRepository {
    /**
     * 执行请求并返回 Result
     * @param request 请求操作
     * @return Result<T> 包装的结果
     */
    suspend fun <T> executeRequest(request: suspend () -> T): Result<T>
}

interface IRemoteDataSource

interface ILocalDataSource
