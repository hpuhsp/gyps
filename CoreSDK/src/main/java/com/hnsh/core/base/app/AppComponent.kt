package com.hnsh.core.base.app

import android.app.Application
import com.hnsh.core.base.app.config.GlobalConfigModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 19:41
 * @UpdateRemark:   更新说明：
 */
@Singleton
@Component(modules = [GlobalConfigModule::class])
interface AppComponent {

    fun inject(delegate: AppDelegate)

    @Component.Builder
    interface HiltFactory {
        @BindsInstance
        fun application(application: Application): HiltFactory

        fun globalConfigModule(globalConfigModule: GlobalConfigModule): HiltFactory

        fun build(): AppComponent
    }
}