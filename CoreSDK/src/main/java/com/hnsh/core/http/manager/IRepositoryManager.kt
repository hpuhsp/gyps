package com.hnsh.core.http.manager

import android.content.Context
import androidx.annotation.NonNull

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/22 17:22
 * @UpdateRemark:   更新说明：
 */
interface IRepositoryManager {
    @NonNull
    fun <T> obtainRetrofitService(@NonNull service: Class<T>): T

    @NonNull
    fun <T> obtainCacheService(@NonNull cache: Class<T>): T

    fun clearAllCache()

    @NonNull
    fun getContext(): Context?
}