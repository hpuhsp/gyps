package com.swallow.fly.base.app.parse

import android.content.Context
import android.content.pm.PackageManager
import com.swallow.fly.base.app.ConfigModule
import java.util.*

/**
 * @Description: 清单文件配置自定义解析类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:49
 * @UpdateRemark:   更新说明：
 */
class ManifestParser(val context: Context) {

    fun parse(): ArrayList<ConfigModule> {
        val modules = ArrayList<ConfigModule>()
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            if (appInfo.metaData != null) {
                for (key in appInfo.metaData.keySet()) {
                    if (MODULE_VALUE == appInfo.metaData[key]) {
                        modules.add(parseModule(key))
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Unable to find metadata to parse ConfigModule", e)
        }
        return modules
    }

    companion object {
        private const val MODULE_VALUE = "ConfigModule"

        private fun parseModule(className: String): ConfigModule {
            val clazz: Class<*>
            clazz = try {
                Class.forName(className)
            } catch (e: ClassNotFoundException) {
                throw IllegalArgumentException(
                    "Unable to find ConfigModule implementation",
                    e
                )
            }
            val module: Any
            module = try {
                clazz.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException(
                    "Unable to instantiate ConfigModule implementation for $clazz",
                    e
                )
            } catch (e: IllegalAccessException) {
                throw RuntimeException(
                    "Unable to instantiate ConfigModule implementation for $clazz",
                    e
                )
            }
            if (module !is ConfigModule) {
                throw RuntimeException("Expected instanceof ConfigModule, but found: $module")
            }
            return module as ConfigModule
        }
    }
}