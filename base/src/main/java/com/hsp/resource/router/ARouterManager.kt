package com.hsp.resource.router

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.launcher.ARouter
import com.swallow.fly.BuildConfig

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 9:50
 * @UpdateRemark:   更新说明：
 */
object ARouterManager {
    /**
     * 可配置模块初始化操作
     */
    fun init(app: Application) {
        ///初始化路由
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
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
     * 必须实现ARouterInjectable接口
     */
    private fun handleActivity(activity: Activity) {
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