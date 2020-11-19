package com.swallow.fly.http.cache

import android.content.SharedPreferences
import com.swallow.fly.ext.prefs.boolean
import com.swallow.fly.ext.prefs.string
import com.swallow.fly.utils.SingletonHolderSingleArg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalShareResource @Inject constructor(private val prefs: SharedPreferences) {
    /**
     * 通用字段，根据各业务模块公共部分可选择性使用
     */
    var accessToken: String by prefs.string("access_token", "")

    var username by prefs.string("username", "")

    var password by prefs.string("password", "")

    var isAutoLogin: Boolean by prefs.boolean("is_login", true)

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String): Long {
        return prefs.getLong(key, Long.MAX_VALUE)
    }

    fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    companion object :
        SingletonHolderSingleArg<LocalShareResource, SharedPreferences>(::LocalShareResource)
}