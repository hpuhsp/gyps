package com.swallow.fly.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.system.exitProcess

/**
 * Activity 管理器
 * 
 * 负责管理应用中所有 Activity 的生命周期和状态
 * 
 * ## 功能特性
 * - Activity 栈管理
 * - 前台 Activity 追踪
 * - Activity 启动和关闭
 * - 应用退出管理
 * 
 * ## 使用示例
 * ```kotlin
 * // 初始化（在 Application 中）
 * AppManager.getInstance().init(this)
 * 
 * // 获取当前 Activity
 * val currentActivity = AppManager.getInstance().currentActivity
 * 
 * // 启动 Activity
 * AppManager.getInstance().startActivity(MainActivity::class.java)
 * 
 * // 关闭所有 Activity
 * AppManager.getInstance().killAll()
 * ```
 * 
 * @author Hsp
 * @email 1101121039@qq.com
 * @since 1.0.0
 * @updated 2024/12 - 迁移到 Kotlin，使用现代化 API
 */
class AppManager private constructor() {

    companion object {
        /**
         * true 为不需要加入到 Activity 容器进行统一管理，默认为 false
         */
        const val IS_NOT_ADD_ACTIVITY_LIST = "is_not_add_activity_list"

        @Volatile
        private var instance: AppManager? = null

        @JvmStatic
        fun getInstance(): AppManager {
            return instance ?: synchronized(this) {
                instance ?: AppManager().also { instance = it }
            }
        }
    }

    private val tag = this::class.java.simpleName

    /**
     * Application 实例
     */
    private var application: Application? = null

    /**
     * 管理所有存活的 Activity
     * 
     * 使用 CopyOnWriteArrayList 保证线程安全，避免并发修改异常
     * 容器中的顺序是 Activity 的创建顺序，并不能保证和 Activity 任务栈顺序一致
     */
    private val activityList = CopyOnWriteArrayList<Activity>()

    /**
     * 当前在前台的 Activity
     */
    @Volatile
    private var currentActivity: Activity? = null

    /**
     * 初始化
     * 
     * @param application Application 实例
     * @return AppManager 实例（支持链式调用）
     */
    fun init(application: Application): AppManager {
        this.application = application
        return this
    }

    /**
     * 让在栈顶的 Activity 打开指定的 Activity
     * 
     * @param intent Intent
     */
    fun startActivity(intent: Intent) {
        val topActivity = getTopActivity()
        if (topActivity == null) {
            Timber.tag(tag).w("topActivity == null when startActivity(Intent)")
            // 如果没有前台的 Activity 就使用 NEW_TASK 模式启动
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application?.startActivity(intent)
            return
        }
        topActivity.startActivity(intent)
    }

    /**
     * 让在栈顶的 Activity 打开指定的 Activity
     * 
     * @param activityClass Activity Class
     */
    fun startActivity(activityClass: Class<*>) {
        application?.let { app ->
            startActivity(Intent(app, activityClass))
        }
    }

    /**
     * 释放资源
     * 
     * 注意：此方法会清空所有 Activity 引用，请谨慎调用
     */
    fun release() {
        activityList.clear()
        currentActivity = null
        application = null
    }

    /**
     * 设置当前在前台的 Activity
     * 
     * 注意：此方法是在 onResume 方法执行时将栈顶的 Activity 赋值给 currentActivity
     * 所以在栈顶的 Activity 执行 onPause 方法时使用 [getCurrentActivity] 获取的就不是当前栈顶的 Activity
     * 如果在 App 启动第一个 Activity 执行 onCreate 方法时使用 [getCurrentActivity] 则会出现返回为 null 的情况
     * 想避免这种情况请使用 [getTopActivity]
     * 
     * @param activity 当前 Activity
     */
    fun setCurrentActivity(activity: Activity?) {
        this.currentActivity = activity
    }

    /**
     * 获取在前台的 Activity
     * 
     * 保证获取到的 Activity 正处于可见状态（即未调用 onPause）
     * 获取的 Activity 存续时间是在 onResume 到 onPause 之间
     * 
     * 使用场景：
     * - 只需要在可见状态的 Activity 上执行的操作
     * - 如当后台 Service 执行某个任务时，需要让前台 Activity 做出某种响应操作
     * - 如弹出 Dialog，这时在 Service 中就可以使用此方法
     * - 如果返回为 null，说明没有前台 Activity（用户返回桌面或者打开了其他 App）
     * 
     * @return 当前在前台的 Activity，可能为 null
     */
    fun getCurrentActivity(): Activity? = currentActivity

    /**
     * 获取最近启动的一个 Activity
     * 
     * 此方法不保证获取到的 Activity 正处于前台可见状态
     * 即使 App 进入后台或在这个 Activity 中打开一个之前已经存在的 Activity
     * 这时调用此方法还是会返回这个最近启动的 Activity，因此基本不会出现 null 的情况
     * 
     * 比较适合大部分的使用场景，如 startActivity
     * 
     * Tips: activityList 容器中的顺序仅仅是 Activity 的创建顺序，并不能保证和 Activity 任务栈顺序一致
     * 
     * @return 最近启动的 Activity，可能为 null
     */
    fun getTopActivity(): Activity? {
        return activityList.lastOrNull()
    }

    /**
     * 返回一个存储所有未销毁的 Activity 的集合
     * 
     * @return Activity 列表（只读）
     */
    fun getActivityList(): List<Activity> = activityList.toList()

    /**
     * 添加 Activity 到集合
     * 
     * @param activity Activity 实例
     */
    fun addActivity(activity: Activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity)
        }
    }

    /**
     * 删除集合里的指定的 Activity 实例
     * 
     * @param activity Activity 实例
     */
    fun removeActivity(activity: Activity) {
        activityList.remove(activity)
    }

    /**
     * 删除集合里的指定位置的 Activity
     * 
     * @param location 位置索引
     * @return 被删除的 Activity，如果索引无效则返回 null
     */
    fun removeActivity(location: Int): Activity? {
        return if (location in activityList.indices) {
            activityList.removeAt(location)
        } else {
            null
        }
    }

    /**
     * 关闭指定的 Activity class 的所有实例
     * 
     * @param activityClass Activity Class
     */
    fun killActivity(activityClass: Class<*>) {
        val iterator = activityList.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass == activityClass) {
                iterator.remove()
                activity.finish()
            }
        }
    }

    /**
     * 指定的 Activity 实例是否存活
     * 
     * @param activity Activity 实例
     * @return true 表示存活
     */
    fun activityInstanceIsLive(activity: Activity): Boolean {
        return activityList.contains(activity)
    }

    /**
     * 指定的 Activity class 是否存活
     * 
     * 同一个 Activity class 可能有多个实例
     * 
     * @param activityClass Activity Class
     * @return true 表示存活
     */
    fun activityClassIsLive(activityClass: Class<*>): Boolean {
        return activityList.any { it.javaClass == activityClass }
    }

    /**
     * 获取指定 Activity class 的实例
     * 
     * 同一个 Activity class 有多个实例，则返回最早创建的实例
     * 
     * @param activityClass Activity Class
     * @return Activity 实例，没有则返回 null
     */
    fun findActivity(activityClass: Class<*>): Activity? {
        return activityList.firstOrNull { it.javaClass == activityClass }
    }

    /**
     * 关闭所有 Activity
     */
    fun killAll() {
        val iterator = activityList.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            iterator.remove()
            activity.finish()
        }
    }

    /**
     * 关闭所有 Activity，排除指定的 Activity
     * 
     * @param excludeActivityClasses 要排除的 Activity Class 数组
     */
    fun killAll(vararg excludeActivityClasses: Class<*>) {
        val excludeSet = excludeActivityClasses.toSet()
        val iterator = activityList.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass !in excludeSet) {
                iterator.remove()
                activity.finish()
            }
        }
    }

    /**
     * 关闭所有 Activity，排除指定的 Activity
     * 
     * @param excludeActivityNames Activity 的完整全路径数组
     */
    fun killAll(vararg excludeActivityNames: String) {
        val excludeSet = excludeActivityNames.toSet()
        val iterator = activityList.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass.name !in excludeSet) {
                iterator.remove()
                activity.finish()
            }
        }
    }

    /**
     * 退出应用程序
     * 
     * 注意：此方法经测试在某些机型上并不能完全杀死 App 进程
     * 几乎试过市面上大部分杀死进程的方式，但都发现效果不佳
     * 所以此方法如果不能百分之百保证能杀死进程，就不能贸然调用 [release] 释放资源
     * 否则会造成其他问题
     */
    fun appExit() {
        try {
            killAll()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "Failed to exit app")
        }
    }

    /**
     * 彻底杀掉 App 的相关进程
     * 
     * 注意：不能先杀掉主进程，否则逻辑代码无法继续执行
     * 需先杀掉相关进程最后杀掉主进程
     */
    fun killAppProcess() {
        application?.let { app ->
            val activityManager = app.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            activityManager?.runningAppProcesses?.forEach { processInfo ->
                if (processInfo.pid != android.os.Process.myPid()) {
                    android.os.Process.killProcess(processInfo.pid)
                }
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }
    }
}
