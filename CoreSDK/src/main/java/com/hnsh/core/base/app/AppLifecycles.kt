package com.hnsh.core.base.app

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull

/**
 * @Description: 代理宿主APP生命周期
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:06
 * @UpdateRemark:   更新说明：
 */
interface AppLifecycle {
    fun attachBaseContext(@NonNull base: Context)

    fun onCreate(@NonNull application: Application)

    fun onTerminate(@NonNull application: Application)
}