package com.swallow.fly.base.app

import android.content.Context
import androidx.annotation.NonNull
import com.swallow.fly.db.DatabaseConfigBuilder
import com.swallow.fly.http.di.NetworkConfigBuilder
import com.swallow.fly.image.ImageConfigBuilder
import com.swallow.fly.log.LogConfigBuilder

/**
 * @Description: 配置模块接口
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:17
 * @UpdateRemark:   现代化升级 - 类型安全的 Builder + 优先级控制
 * 
 * 使用说明：
 * 1. 实现此接口来配置框架的各个模块
 * 2. 在 AndroidManifest.xml 中注册：
 *    <meta-data
 *        android:name="your.package.YourConfiguration"
 *        android:value="ConfigModule" />
 * 3. 通过 priority() 方法控制配置优先级（数字越大优先级越高）
 * 4. 只需要覆盖需要配置的方法，其他方法使用默认实现
 */
interface ConfigModule {

    /**
     * 配置优先级
     * 
     * 数字越大优先级越高，高优先级的配置会覆盖低优先级的配置
     * 
     * 建议：
     * - app 模块：100（最高优先级）
     * - 业务模块：50-99
     * - 基础模块：0-49
     * 
     * @return 优先级，默认为 0
     */
    fun priority(): Int = 0

    /**
     * 配置网络层
     * 
     * @param context 上下文
     * @param builder 网络配置构建器，支持链式调用
     */
    fun configureNetwork(
        @NonNull context: Context,
        @NonNull builder: NetworkConfigBuilder
    ) {
        // 默认空实现
    }

    /**
     * 配置数据库
     * 
     * @param context 上下文
     * @param builder 数据库配置构建器
     */
    fun configureDatabase(
        @NonNull context: Context,
        @NonNull builder: DatabaseConfigBuilder
    ) {
        // 默认空实现
    }

    /**
     * 配置日志
     * 
     * @param context 上下文
     * @param builder 日志配置构建器
     */
    fun configureLog(
        @NonNull context: Context,
        @NonNull builder: LogConfigBuilder
    ) {
        // 默认空实现
    }

    /**
     * 配置图片加载
     * 
     * @param context 上下文
     * @param builder 图片配置构建器
     */
    fun configureImage(
        @NonNull context: Context,
        @NonNull builder: ImageConfigBuilder
    ) {
        // 默认空实现
    }

    /**
     * 注入模块生命周期
     * 
     * @param context 上下文
     * @param lifecycleList 生命周期列表
     */
    fun injectModulesLifecycle(
        @NonNull context: Context,
        @NonNull lifecycleList: ArrayList<AppLifecycle>
    ) {
        // 默认空实现
    }
}