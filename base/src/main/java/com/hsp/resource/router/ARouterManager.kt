package com.hsp.resource.router

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.launcher.ARouter
//import com.hsp.resource.BuildConfig

/**
 * @Description: ARouter 管理器（已禁用）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 9:50
 * @UpdateRemark:   ARouter 已废弃，此类暂时禁用。如需使用路由功能，建议使用 Navigation Component
 *
 * 注意：ARouter 库在 Maven Central 上不可用（jcenter 已关闭）
 * 如需启用，请手动下载 arouter-api.jar 并放入 libs 目录
 */
@Deprecated("ARouter is deprecated. Use Navigation Component instead.")
object ARouterManager {
    /**
     * 可配置模块初始化操作（已禁用）
     */
    fun init(app: Application) {
        // ARouter 已禁用
        // 如需使用路由功能，建议迁移到 Navigation Component
        ///初始化路由
//        if (BuildConfig.DEBUG) {
//            ARouter.openLog()
//            ARouter.openDebug()
//        }
        ARouter.init(app)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }

    /**
     * 必须实现ARouterInjectable接口（已禁用）
     */
    private fun handleActivity(activity: Activity) {
        // ARouter 已禁用
        if (activity is ARouterInjectable) {
            ///注入ARouter参数
            ARouter.getInstance().inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        savedInstanceState: Bundle?
                    ) {
                        if (f is ARouterInjectable) {
                            ///注入ARouter参数
                            ARouter.getInstance().inject(f)
                        }
                    }
                }, true
            )
        }
    }
}