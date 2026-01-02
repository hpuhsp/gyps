package com.swallow.fly.http.manager

import android.content.Context
import androidx.annotation.NonNull

/**
 * @Description: Repository 管理器接口
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/22 17:22
 * @UpdateRemark:   
 *   - 2026/01: 重命名 obtainRetrofitService 为 obtainService，解耦技术栈
 */
interface IRepositoryManager {
    
    /**
     * 获取网络服务接口（推荐使用）
     * 
     * @param service 服务接口的 Class 对象
     * @return 服务接口的实现实例
     */
    @NonNull
    fun <T> obtainService(@NonNull service: Class<T>): T
    
    /**
     * 获取 Retrofit 服务接口（已废弃）
     * 
     * @deprecated 请使用 obtainService 替代，新方法不绑定特定网络库
     */
    @Deprecated(
        message = "Use obtainService instead. This method name is coupled to Retrofit.",
        replaceWith = ReplaceWith("obtainService(service)"),
        level = DeprecationLevel.WARNING
    )
    @NonNull
    fun <T> obtainRetrofitService(@NonNull service: Class<T>): T

    @NonNull
    fun <T> obtainCacheService(@NonNull cache: Class<T>): T

    fun clearAllCache()

    @NonNull
    fun getContext(): Context?
}