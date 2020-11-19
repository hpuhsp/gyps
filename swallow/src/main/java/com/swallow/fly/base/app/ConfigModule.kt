package com.swallow.fly.base.app

import android.content.Context
import androidx.annotation.NonNull
import com.swallow.fly.base.app.config.GlobalConfigModule

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:17
 * @UpdateRemark:   更新说明：
 */
interface ConfigModule {

    fun applyOptions(
        @NonNull context: Context?,
        @NonNull builder: GlobalConfigModule.Builder
    )

    /**
     * 可获取宿主App的上下文对象，进行后续操作
     */
    fun injectModulesLifecycle(
        @NonNull context: Context,
        @NonNull lifecycleList: ArrayList<AppLifecycle>
    )
}